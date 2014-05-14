/**
 * 根据名称取一个对象
 * */
function $N(obj_name) {
	return document.getElementsByName(obj_name)[0];
}
/**
 * 根据名称取对象的value值
 * */
function $NF(obj_name) {
	var obj = $N(obj_name);
	return obj.value.Trim();
}

function checkLinkname(obj_name) {
	var obj = $N(obj_name);
	var pname = $NF(obj_name);
	var name = $NF('NAME');
	if(pname == name) {
		alert("第三联系人姓名不能与本人姓名相同！");
		obj.select();
		//obj.focus();
		return false;
	}
	return true;
}

function checkLinkPhone(obj_name) {
	var obj = $N(obj_name);
	var linkPhone = $NF(obj_name);
	var phone = $NF('PHONE1');
	if(phone == linkPhone) {
		alert("第三联系人手机号码不能与本人手机号码相同！");
		obj.select();
		//obj.focus();
		return false;
	}
	return true;
}

function checkAmt() {
	var v_AMT = $NF('TOTALAMT');
	var v_RECEIVEAMT = $NF('RECEIVEAMT');

	if (v_AMT >= 10000) {
		var v_r = Math.floor(v_AMT * 0.2);
		if(v_RECEIVEAMT < v_r) {
			alert('首付款不得少于' + v_r + "元！");
			$N('RECEIVEAMT').select();
			return false;
		}
	}
	return true;
}