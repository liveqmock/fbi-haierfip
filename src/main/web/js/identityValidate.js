function isIdCardNo(numObj)
{
    var num;
    if (typeof(numObj) == "object") {
        numObj.value = trimStr(numObj.value);
        num = trimStr(numObj.value);        
    }
    else if (numObj != undefined) num = numObj;
    var factorArr = new Array(7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2,1);
    var error;
    var varArray = new Array();
    var intValue;
    var lngProduct = 0;
    var intCheckDigit;
    var intStrLen = num.length;
    var idNumber = num;
    // initialize
    if (num != ""){
        if ((intStrLen != 15) && (intStrLen != 18)) {
        error = "输入身份证号码长度不对！";
        alert(error);
        numObj.focus();
        numObj.select();
        return false;
        }
        // check and set value
        for(i=0;i<intStrLen;i++) {
            varArray = idNumber.charAt(i);
            if ((varArray < '0' || varArray > '9') && (i != 17)) {
                error = "错误的身份证号码！.";
                alert(error);
                numObj.focus();
                numObj.select();
                return false;
            }
        }
        if (intStrLen == 18) {
            //check date
            var date8 = idNumber.substring(6,14);
            date8 = date8.substring(0,4) + "-" + date8.substring(4,6) + "-" + date8.substring(6,8);
            if (checkDate(date8) == false) {
                
                numObj.focus();
                numObj.select();
                return false;
            }
            // calculate the sum of the products
            for(i=0;i<17;i++) {
                lngProduct = lngProduct + (idNumber.charAt(i))*factorArr[i];
            }

            // calculate the check digit
            intCheckDigit = 12 - lngProduct % 11;
            switch (intCheckDigit) {

                case 10:
                    intCheckDigit = 'X';
                    break;
                case 11:
                    intCheckDigit = 0;
                    break;
                case 12:
                    intCheckDigit = 1;
                    break;
            }
            // check last digit

            if (idNumber.charAt(17).toUpperCase() != intCheckDigit) {
                error = "身份证效验位错误!...正确为： " + intCheckDigit + ".";
                alert(error);
                numObj.focus();
                numObj.select();
                return false;
            }
        }
        else{        //length is 15
            //check date
            var date6 = idNumber.substring(6,12);
            date6 = "19" + date6.substring(0,2) + "-" + date6.substring(2,4) + "-" + date6.substring(4,6);
            if (checkDate(date6) == false) {
//                alert("身份证日期信息有误！.");
                numObj.focus();
                numObj.select();
                return false;
            }

        }
    }

}
