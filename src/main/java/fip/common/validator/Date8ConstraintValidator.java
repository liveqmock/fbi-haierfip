package fip.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-11-14
 * Time: 17:09:01
 * To change this template use File | Settings | File Templates.
 */
public class Date8ConstraintValidator implements ConstraintValidator<Date8, String> {
    public void initialize(Date8 constraint) {
    }

    public boolean isValid(String obj, ConstraintValidatorContext context) {
        if (obj == null || "".equals(obj)) {
            return true;
        }
        
        boolean result = true;
        try {
            Date date = new SimpleDateFormat("yyyyMMdd").parse(obj);
        } catch (ParseException e) {
            result = false;
        }
        return result;
    }
}
