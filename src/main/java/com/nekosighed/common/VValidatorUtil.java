package com.nekosighed.common;

import com.nekosighed.common.exception.ParamErrorException;
import org.hibernate.validator.HibernateValidator;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 校验工具类
 */
public class VValidatorUtil {

    /**
     * 普通模式下的 validator， 会返回全部错误
     */
    private static Validator normalValidator;

    /**
     * 设置为 快速失败返回的 validator
     */
    private static Validator fastFailValidator;


    static {
        // 普通模式: 非快速失败
        normalValidator = Validation.buildDefaultValidatorFactory().getValidator();

        // 快速失败模式
        // check https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#section-provider-specific-settings
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty("hibernate.validator.fail_fast", "true")
                .buildValidatorFactory();
        fastFailValidator = validatorFactory.getValidator();
    }

    /**
     * 工具类的构造方法必须私有
     */
    private VValidatorUtil(){}

    /**
     * 获取 BindingResult 的错误处理
     * validator 需要调用方自己确认是何种方式
     * 参考 https://www.cnblogs.com/mr-yang-localhost/p/7812038.html#_lab2_2_0 修改容器中的 validator
     *
     * @param result BindingResult
     * @exception ParamErrorException   校验不通过，则抛出 ParamErrorException 异常
     */
    public static void checkBindResult(BindingResult result){
        if (Objects.nonNull(result) && result.hasErrors()){
            StringBuilder builder = new StringBuilder();
            List<ObjectError> errors = result.getAllErrors();
            errors.forEach(error-> builder.append(error.getDefaultMessage()).append("\n"));
            throw new ParamErrorException(builder.toString());
        }
    }

    /**
     * 校验对象
     *
     * @param object                    待校验对象
     * @param isNormal                  是否使用普通模式
     * @param groups                    代校验组
     * @exception ParamErrorException   校验不通过，则抛出 ParamErrorException 异常
     */
    public static void checkValidateEntity(Object object, Boolean isNormal, Class<?> ... groups){
        Validator validator = normalValidator;
        if (!isNormal){
            validator = fastFailValidator;
        }
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()){
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<Object> constraintViolation: constraintViolations){
                builder.append(constraintViolation.getMessage()).append("\n");
            }
            throw new ParamErrorException(builder.toString());
        }
    }

    /**
     * 获得一个 fastFailValidator
     *
     * @param showMessage 是否显示该 validator 如何创建(简单控制台打印)
     * @return 一个 FastFail 的 Validator
     */
    public static Validator getFastFailValidator(Boolean showMessage) {
        if (showMessage){
            System.out.println("ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)\n" +
                    "                .configure()\n" +
                    "                .addProperty(\"hibernate.validator.fail_fast\", \"true\")\n" +
                    "                .buildValidatorFactory();\n" +
                    "          Validator  fastFailValidator = validatorFactory.getValidator();");
        }
        return fastFailValidator;
    }
}
