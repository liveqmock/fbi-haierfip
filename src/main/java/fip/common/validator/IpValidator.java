package fip.common.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-11-5
 * Time: 10:41:13
 * To change this template use File | Settings | File Templates.
 */
@FacesValidator(value = "ipValidator")
public class IpValidator implements Validator {

    private static final String IP_REGEX = "^([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$";

    public void validate(FacesContext context, UIComponent component,
                         Object value) throws ValidatorException {
        String ipAddress = (String) value;
        Pattern mask = null;
        mask = Pattern.compile(IP_REGEX);
        Matcher matcher = mask.matcher(ipAddress);
        if (!matcher.matches()) {
            FacesMessage message = new FacesMessage();
            message.setDetail("IP µÿ÷∑ ‰»Î¥ÌŒÛ£°");
            message.setSummary("IP µÿ÷∑ ‰»Î¥ÌŒÛ£°");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }
}
