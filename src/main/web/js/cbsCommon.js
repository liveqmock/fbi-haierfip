/*金额输入项限制*/
        function amtKeydown(obj) {
            var number = obj.value;
            if (isNaN(number)) {
                obj.value = number.substr(0, number.length - 1);
                number = obj.value;
            } else if (number.indexOf(".") != -1) {
                if ((number.length - number.lastIndexOf(".")) > 3) {
                    obj.value = number.substr(0, number.length - 1);
                }
            } else if (number.length > 13) {
                obj.value = number.substr(0, number.length - 1);
            }
        }

        function amtBlur(obj) {
            var number = obj.value;

            if (isNaN(number)) {
                obj.value = "";
                return false;
            }
            if (number != "") {
                if (number.indexOf(".") == -1) {
                    number += ".00";
                } else {
                    if ((number.length - number.lastIndexOf(".")) == 1) {
                        number += "00";
                    } else if ((number.length - number.lastIndexOf(".")) == 2) {
                        number += "0";
                    }
                }
                obj.value = number;
            }
        }

        function amtKeypress(obj) {
            if ((48 > event.keyCode || event.keyCode > 57) && (event.keyCode != 46) && (event.keyCode != 45)) {
                event.returnValue = false;
            }
        }
        /**
         * 判断输入数字*/
        function numBlur(obj) {
            if (isNaN(obj.value)) {
                obj.value = "";
            }
        }