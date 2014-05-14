/**
 * 通用函数。根据Id，判断指定Tab页内容是否有效。
 * 检查数据窗口数据
 * @param Id 窗口Id
 */
function validateTabPage( Id )
{
	var objError;
	objError=eval( "dw_"+Id+".validate( true )");
	if (objError!=null)
	{
		tp.selectTabPageById(Id);
		alert( DataWindow.alertMsg );
		DataWindow.setFocus(objError);
		return false;
	}
	else
		return true;
}


/**
 * 校验经营期限是否有效。检查规则为：yyqx1 < yyqx2 & yyqx2 > current Date
 *
 * @return boolean
 *  true 有效
 *  false 无效
 */
function validateYYQX()
{
	var ls_YYQX1=dw_jb.getItemString(0,"YYQX1");
	var ls_YYQX2=dw_jb.getItemString(0,"YYQX2");
	if (ls_YYQX2!="")
	{
		if (dw_jb.getItemString(0,"YYQX2")<dw_jb.getItemString(0,"YYQX1"))
		{
			alert( "经营期限范围不对，经营期限结束时间小于经营期限起始时间！" );
			tp.selectTabPageById("jb");
			dw_jb.setColumn( "YYQX2" );
			return false;
		}
		if (ls_YYQX2<is_CURDATE)
		{
			alert( "营业期限至小于当前日期，请检查！" );
			tp.selectTabPageById("jb");
			dw_jb.setColumn( "YYQX2" );
			return false;
		}
	}
	return true;
}

/**
 * 校验经营期限是否有效。检查规则为：zzqxq < zzqxz & zzqxz > current Date
 *
 * @return boolean
 *  true 有效
 *  false 无效
 */
function validateZZQX()
{
	if (is_QYLB=="2")
	{
		var ls_ZZQXQ=dw_jb.getItemString(0,"ZZQXQ");
		var ls_ZZQXZ=dw_jb.getItemString(0,"ZZQXZ");
		if (ls_ZZQXZ!="")
		{
			if (ls_ZZQXZ<ls_ZZQXQ)
			{
				tp.selectTabPageById("jb");
				alert( "执照有效期限范围不对，期限截至时间小于起始时间！" );
				dw_jb.setColumn("ZZQXZ");
				return false;
			}
			if (ls_ZZQXZ<is_CURDATE)
			{
				alert( "执照期限截止日期小于当前日期，请检查！" );
				tp.selectTabPageById("jb");
				dw_jb.setColumn("ZZQXZ");
				return false;
			}
		}
	}
	return true;
}

/**
 * 检查出资表中最后一条出资记录。规则是： qxz > current Date
 *
 * @return boolean
 *  true 有效
 *  false 无效
 */
function validateCZQX()
{
	var nCount=dw_cz.RowCount();
	if (nCount>0)
	{
		var ls_MaxQS=findMaxValue( dw_cz, "QS" );
		var nRow    =findRow(dw_cz,"QS",ls_MaxQS);
		var ls_QXZ=dw_cz.getItemString(nRow,"QXZ");
		if (ls_QXZ<is_CURDATE)
		{
			tp.selectTabPageById("cz");
			alert( "出资期限至小于当前日期，请检查！" );
			dw_cz2.setColumn("QXZ");
			return false;
		}
	}
	return true;
}

/**
 * 检测GS和SYQY的出资额情况是否有效。规则是：法人股东、自然人股东出资之和=ZCZB
 *
 * @return boolean
 *  true 有效
 *  false 无效
 */
function validateCZE()
{
	if (CV_isChecked("CZE",hashCode(""))!=-1) return true;	//已经检查过了
	if (is_QYLXDL!="GS" && is_QYLXDL!="SYQY" ) return true;
	var ld_total_cze=0;
	var nFRCount=dw_fr.RowCount();
	for (var i=0; i<nFRCount; i++)
	{
		if (!dw_fr.isValidRow(i)) continue;
		ld_total_cze+=dw_fr.getItemFloat(i,"CZE");
	}
	var nZRCount=dw_zr.RowCount();
	for( var i=0; i<nZRCount; i++)
	{
		if (!dw_zr.isValidRow(i)) continue;
		ld_total_cze+=dw_zr.getItemFloat(i,"CZE");
	}
	var ld_zczb=dw_jb.getItemFloat(1,"ZCZB");

	ld_zczb=new Number(ld_zczb).toFixed(2);
	ld_total_cze=new Number(ld_total_cze).toFixed(2);
	
	if (ld_zczb>0)
	{
		if (ld_total_cze!=ld_zczb)
		{
			var ls_Msg="注册资本不等于法人股东和自然人股东出资额总和，是否允许通过？\n注册资本（折美元）："+ld_zczb+"  出资总和（折美元）："+ld_total_cze;
			if (confirm(ls_Msg))
			{
				CV_fillOne( is_BH, "CZE", hashCode(""), "0", "注册资本不等于法人股东和自然人股东出资额总和，经允许通过检查。\n注册资本（折美元）："+ld_zczb+"  出资总和（折美元）："+ld_total_cze );
				return true;
			}
			else
			{
				if (nFRCount>0) tp.selectTabPageById("fr");
				else tp.selectTabPageById("zr");
				return false;
			}
		}
	}
	return true;
}

/**
 * 校验注册资本是否有效。检查规则为：zczb > 0
 *
 * @return boolean
 *  true 有效
 *  false 无效
 */
function validateZCZB()
{
	//FGS, WZBS, WGDB没有注册资本录入项目，不检测该项
	if (is_QYLXDL=="FGS" || is_QYLXDL=="WZBS" || is_QYLXDL=="WGDB" ) return true;
	var ld_zczb=dw_jb.getItemFloat(0,"ZCZB");
	var ls_zczbname=dw_jb.getColumnCName("ZCZB");
	if (ld_zczb<0)
	{
		tp.selectTabPageById("jb");
		alert( ls_zczbname+'不能是负数！');
		dw_jb.setColumn("ZCZB");
		return false;
	}
	if (is_QYLXDL=="WZQY" && isWZDZ(is_QYLX))
	{
		if (dw_jb.getItemFloat(1,"WFRJCZMY")!=dw_jb.getItemFloat(1,"ZCZBMY"))
		{
			tp.selectTabPageById("jb");
			alert("独资企业注册资本应该等于外方认缴额，请检查！");
			dw_jb.setColumn("ZCZB");
			return false;
		}
	}
	return true;
}

/**
 * GS,SYQY,SYFZ检查认缴资本是否有效。检查规则为：sjzb<=zczb
 * WZQY校验外方认缴出资额是否有效。检查规则为：wfrjczmy<=zczbmy
 *
 * @return boolean
 *  true 有效
 *  false 无效
 */
function validateSJZB()
{
	switch( is_QYLXDL )
	{
		case "GS":
		case "SYQY":
		case "SYFZ":
			if (dw_jb.getItemFloat(1,"SJZB")>dw_jb.getItemFloat(1,"ZCZB"))
			{
				tp.selectTabPageById("jb");
				alert( "认缴资本额大于注册资本额，请修改!" );
				dw_jb.setColumn("SJZB");
				return false;
			}
			break;
		case "WZQY":
			if (dw_jb.getItemFloat(1,"WFRJCZMY")>dw_jb.getItemFloat(1,"ZCZBMY"))
			{
				tp.selectTabPageById("jb");
				alert( "外方认缴资本额大于注册资本额，请修改!" );
				dw_jb.setColumn("WFRJCZE");
				return false;
			}
			break;
	}
	return true;
}

/**
 * 校验出资表信息是否有效。检查规则为：
 * 每条记录都检测，wfmy<=wfyj 外方实缴美元<=外方应交美元
 *
 * @return boolean
 *  true 有效
 *  false 无效
 */
function validateCZQK()
{
	var ld_wfycz=0;
	var ld_zfycz=0;
	//不是外资企业，返回正确
	if (is_QYLXDL!="WZQY")	return true;
	var nCZCount=dw_cz.RowCount();
	for( var i=0; i<nCZCount; i++)
	{
		if (dw_cz.getItemFloat(i,"WFYJ")<dw_cz.getItemFloat(i,"WFMY"))
		{
			tp.selectTabPageById("cz");
			dw_cz.selectRow(i);
			alert( "外方实出资折合美元数应小于外方应出资，请修改" );
			dw_cz2.setColumn("WFMY");
			return false;
		}
		if (dw_cz.getItemFloat(i,"ZFYJ")<dw_cz.getItemFloat(i,"ZFMY"))
		{
			tp.selectTabPageById("cz");
			dw_cz.selectRow(i);
			alert( "中方实出资折合美元数应小于中方应出资，请修改" );
			dw_cz2.setColumn("ZFMY");
			return false;
		}
		ld_wfycz+=dw_cz.getItemFloat(i,"WFYJ");
		ld_zfycz+=dw_cz.getItemFloat(i,"ZFYJ");
	}
	if (CV_isChecked("CZQK",hashCode(""))!=-1) return true; //已经检查过了
	var ld_wfrjczmy=dw_jb.getItemFloat(1,"WFRJCZMY");
	if (Math.abs(ld_wfrjczmy-ld_wfycz/1)>0)
	{
		var ls_Msg="外方应出资不等于基本情况中外方认缴额折合美元数，可以通过吗？\n应出资总和为："+ld_wfycz+"  外方认缴额折美元为："+ld_wfrjczmy;
		if (confirm(ls_Msg))
		{
			CV_fillOne(is_BH, "CZQK", hashCode(""), "0", "外方应出资不等于基本情况中外方认缴额折合美元数，经允许通过检查。\n应出资总和为："+ld_wfycz+"  外方认缴额折美元为："+ld_wfrjczmy );
			return true;
		}
		else
		{
			tp.selectTabPageById("cz");
			dw_cz2.setColumn("WFYJ");
			return false;
		}
	}
	return true;
}


/**
 * 校验投资总额是否有效。检查规则为：如果是独资企业，tzzemy=zczbmy
 *
 * @return boolean
 *  true 有效
 *  false 无效
 */
function validateTZZE()
{
	var ls_qylx=dw_jb.getItemString(0,"QYLX");
	if (is_QYLXDL=="WZQY" && !isWZGF(is_QYLX))	//非股份公司
	{
		if (dw_jb.getItemFloat(1,"TZZEMY")<dw_jb.getItemFloat(1,"ZCZBMY"))
		{
			tp.selectTabPageById("jb");
			alert("投资总额小于注册资本，请检查！");
			dw_jb.setColumn("TZZE");
			return false;
		}
	}
	return true;
}


/**
 * 检测法人股东的法定代表人是否出现在自然人股东中。
 *
 * @return boolean
 *  true 有效
 *  false 无效
 */
function validateFRFDDBR()
{
	if (tp.existTabPageById("fr") && tp.existTabPageById("zr"))	//两者都存在，才检测
	{
		var nFRCount=dw_fr.RowCount();
		for( var i=0; i<nFRCount; i++ )
		{
			var ls_fddbr=dw_fr.getItemString(i,"FDDBR");
			var nZRCount=dw_zr.RowCount();
			for( var j=0; j<nZRCount; j++ )
			{
				if (!dw_zr.isValidRow(j)) continue;
				var ls_xm=dw_zr.getItemString(j,"XM");
				if (dw_fr.isValidRow(i) &&ls_xm==ls_fddbr)
				{
					tp.selectTabPageById("fr");
					dw_fr.selectRow(i);
					alert("法人股东的法定代表人出现在自然人股东里，请检查！\n该法定代表人的姓名为："+ls_fddbr);
					dw_fr2.setColumn("FDDBR");
					return false;
				}
			}
		}
		return true;
	}
	return true;
}

/**
 * 检测公司董事会成员人数是否齐全
 *
 * @return boolean
 *  true 有效
 *  false 无效
 */
function validateGSDSH()
{
	if (is_QYLXDL=="GS" || is_QYLXDL=="NZFR" || is_QYLXDL=="SYQY" || is_QYLXDL=="WZQY" )
	{
		var QYLXTEXT=dw_jb.getItemExpand(1,"QYLX","text");
		if (QYLXTEXT.indexOf("有限")>=0 && QYLXTEXT.indexOf("公司")>=0 )
		{
			var ZWSArr=new Array(0,0,0,0,0);	//0董事、1经理、2监事、3董事长、4执行董事
			nDSCount=dw_ds.RowCount();
			for( var i=0; i<nDSCount; i++ )
			{
				if (!dw_ds.isValidRow(i)) continue;
				var sZW=dw_ds.getItemString(i,"ZW");
				switch( sZW )
				{
					case "01":	//01董事长
						ZWSArr[0]=ZWSArr[0]+1;
						ZWSArr[3]=ZWSArr[3]+1;
						break;
					case "02":  //02副董事长
					case "11":	//11董事
						ZWSArr[0]=ZWSArr[0]+1;
					   	break;
					case "03":  //03总经理
					case "04":  //04副总经理
					case "12":	//12经理
						ZWSArr[1]=ZWSArr[1]+1;
					   	break;
					case "05":  //05执行董事
						ZWSArr[0]=ZWSArr[0]+1;
						ZWSArr[4]=ZWSArr[4]+1;
						break;
				 	case "06":	//06监事 	
				 	case "13":	//13监事召集人
					   ZWSArr[2]=ZWSArr[2]+1;
					   var ls_XM=dw_ds.getItemString(i, "XM");
					   for( var j=0; j<nDSCount; j++)
					   {	//监事必须是独立的,董事长/总经理/执行董事不能兼任监事,现在没有检查
					   		var ls_XM2=dw_ds.getItemString(j,"XM");
					   		var ls_ZW2=dw_ds.getItemString(j,"ZW");
					   		if (ls_XM==ls_XM2 && (ls_ZW2=="01"||ls_ZW2=="03"||ls_ZW2=="05"||ls_ZW2=="14"||ls_ZW2=="15"))
					   		{
					   			alert( "董事长、总经理、执行董事不能兼任监事！" );
								tp.selectTabPageById("ds");
								dw_ds.selectRow(j);
					   			return false;
					   		}
					   }
					   break;
					case "14":	//14董事长兼总经理
						ZWSArr[0]=ZWSArr[0]+1;
						ZWSArr[3]=ZWSArr[3]+1;
						ZWSArr[1]=ZWSArr[1]+1;
						break;
					case "15":	//15执行董事兼总经理
						ZWSArr[0]=ZWSArr[0]+1;
						ZWSArr[4]=ZWSArr[4]+1;
						ZWSArr[1]=ZWSArr[1]+1;
						break;
				}
			}
			if (is_QYLXDL=="WZQY" && isWZDZ(is_QYLX))	//外商独资企业
			{
				//只有一名执行董事，允许通过
				if (dw_ds.RowCount()==1 && ZWSArr[4]==1) return true;
			}
			if (ZWSArr[3]>1)
			{
				tp.selectTabPageById("ds");
				alert( "董事长不能有多名！" );
				return false;
			}
			if (ZWSArr[3]>0 && ZWSArr[0]<3)	//有董事长，并且董事<3人
			{
				tp.selectTabPageById("ds");
				alert( "有董事长时，董事会成员至少需要三人！" );
				return false;
			}
			if (ZWSArr[0]==0)
			{
				tp.selectTabPageById("ds");
				alert("有限公司成员必须要有董事！")
				return false;
			}
			if (ZWSArr[1]==0)
			{
				tp.selectTabPageById("ds");
				alert("有限公司成员必须要有经理！");
				return false;
			}
		   	if (ZWSArr[2]==0)
		   	{
				tp.selectTabPageById("ds");
				alert("有限公司成员必须要有监事！");
				return false;
			}
		}
	}
	return true;
}


/**
 * 对外资企业进行检查：管辖单位和根据区县名计算的管辖单位进行判断。
 *
 */
function vlidateGXDW()
{
	if (CV_isChecked("GXDW",hashCode(""))!=-1) return true;	//已经检查过了
	if (is_QYLB!="2") return true;
	var ls_QXM=dw_jb.getItemString(0,"QXM");
	var ls_QXMEXPAND=dw_jb.getItemExpand(1,"QXM");
	var dqdmArr=ls_QXMEXPAND.split("-");
	if (dqdmArr.length==0) return true;	//通过检查
	var ls_GXDW=dw_jb.getItemString(0,"GXDW");
	if (ls_GXDW!=dqdmArr[0])
	{
		var ls_msg="根据区县名确定的管辖单位与现在的选择不一致，是否允许通过检查？\n"+
				"根据该企业的区县名：" + ls_QXM + "，该企业的管辖单位应该是：" + dqdmArr[0] +"\n"+
				"现有的值管辖单位是："+ls_GXDW;
		if (confirm(ls_msg))
		{
			CV_fillOne(is_BH, "GXDW", hashCode(""), "0",
				"根据区县名确定的管辖单位与现在的选择不一致，经允许通过检查。\n"+
				"根据该企业的区县名：" + ls_QXM + "，该企业的管辖单位应该是：" + dqdmArr[0] +"\n"+
				"现有的值管辖单位是："+ls_GXDW);
			return true;
		}
		else
		{
			tp.selectTabPageById("jb");
			return false;
		}
	}
	return true;
}

/**
 * 检测各个表的主键是否冲突。
 *
 * @return boolean
 *  true 有效
 *  false 无效
 */
function validatePrimaryKey( tpName )
{
	switch( tpName )
	{
		case "sp":	//审批文件wjmc
			if (!dw_sp.validatePrimaryKey("WJMC")) { tp.selectTabPageById("sp"); return false; }
			break;
		case "tz":	//投资双方qymc
			if (!dw_tz.validatePrimaryKey("QYMC")) { tp.selectTabPageById("tz"); return false; }
			break;
		case "cz":	//出资情况qs
			if (!dw_cz.validatePrimaryKey("QS")) { tp.selectTabPageById("cz"); return false; }
			break;
		case "fr":	//法人股东qymc
			if (!dw_fr.validatePrimaryKey("QYMC")) { tp.selectTabPageById("fr");return false; }
			break;
		case "zr":	//自然人股东xm,zjhm
			if (!dw_zr.validatePrimaryKey("XM,ZJHM")) { tp.selectTabPageById("zr");return false; }
			break;
		case "ds":	//董事会成员、外资企业成员xm,zw,zjhm
			if (!dw_ds.validatePrimaryKey("XM,ZW,ZJHM")) { tp.selectTabPageById("ds");return false; }
			break;
		case "fz":	//分支机构qymc
			if (!dw_fz.validatePrimaryKey("QYMC")) { tp.selectTabPageById("fz");return false; }
			break;
		case "wt":	//授权委托书wtrlx
			if (!dw_wt.validatePrimaryKey("WTRLX")) { tp.selectTabPageById("wt");return false; }
			break;
		case "yj":	//意见表xh
			if (!dw_yj.validatePrimaryKey("XH")) { tp.selectTabPageById("yj");return false; }
			break;
		case "jc":	//审批检查结果lx,xh
			if (!dw_jc.validatePrimaryKey("LX,XH")) { tp.selectTabPageById("jc");return false; }
			break;
		default:
			return true;
			break;
	}
	return true;
}

/**
 * 校验记录数是否符合条件。检测规则：有些企业必须填写某些记录。
 *
 * @return boolean
 *  true 有效
 *  false 无效
 */
function validateRowCount()
{
	var ls_QYLB=dw_jb.getItemString(0,"QYLB");
	if (ls_QYLB=="1")
	{
		if (tp.existTabPageById("sp"))
		{
			if (dw_sp.RowCount(true)==0)
			{
				alert("该企业的审批文件没有录入，请检查输入！");
				tp.selectTabPageById("sp");
				return false;
			}
		}
		if (tp.existTabPageById("fr") && tp.existTabPageById("zr"))
		{
			if (dw_fr.RowCount(true)==0 && dw_zr.RowCount(true)==0)
			{
				alert("该企业的股东信息没有录入，请检查录入！");
				tp.selectTabPageById("fr");
				return false;
			}
		}
		if (tp.existTabPageById("ds"))
		{
			if (dw_ds.RowCount(true)==0)
			{
				alert("该企业的公司成员没有录入，请检查输入！");
				tp.selectTabPageById("ds");
				return false;
			}
		}
		if (tp.existTabPageById("wt"))
		{
			if (dw_wt.RowCount(true)==0)
			{
				alert("该企业的委托书没有录入，请检查输入！" );
				tp.selectTabPageById("wt");
				return false;
			}
		}
	}
	else if (ls_QYLB=="2")
	{
		if (is_QYLXDL!="WGDB")	//特例：WGDB不检查审批文件
		{
			if (tp.existTabPageById("sp"))
			{
				if (dw_sp.RowCount(true)==0)
				{
					alert("该企业的审批文件没有录入，请检查输入！");
					tp.selectTabPageById("sp");
					return false;
				}
			}
		}
		if (tp.existTabPageById("tz"))
		{
			if (dw_tz.RowCount(true)==0)
			{
				alert("该企业的投资方没有录入，请检查输入！");
				tp.selectTabPageById("tz");
				return false;
			}
		}
		/*
		if (tp.existTabPageById("cz") && is_YWLX!="1")
		{
			if (dw_cz.RowCount(true)==0)
			{
				alert("该企业的出资情况没有录入，请检查输入！");
				tp.selectTabPageById("cz");
				return false;
			}
		}
		*/
		if (tp.existTabPageById("ds"))
		{
			if (dw_ds.RowCount(true)==0)
			{
				alert("该企业的企业成员没有录入，请检查输入！");
				tp.selectTabPageById("ds");
				return false;
			}
		}
	}
	return true;
}


/**
 * 检查人员的证件号码
 */
function validateZJHM()
{
	var ls_ZJMC, ls_ZJHM;
	if (is_QYLXDL=="WGJY" || is_QYLXDL=="WGFZ" )
	{
		ls_ZJMC=dw_jb.getItemString(0, "FZRSFZJ");
		ls_ZJHM=dw_jb.getItemString(0, "FZRZJHM");
		if (ls_ZJMC=="01")	//身份证
			if (!validatezjhm(ls_ZJHM))
			{
				tp.selectTabPageById("jb");
				dw_jb.setColumn("FZRZJHM");
				return false;
			}
	}
	if (tp.existTabPageById("fzr"))
	{
		ls_ZJMC=dw_fzr.getItemString(0, "ZJMC");
		ls_ZJHM=dw_fzr.getItemString(0, "ZJHM");
		if (ls_ZJMC=="01")	//身份证
			if (!validatezjhm(ls_ZJHM))
			{
				tp.selectTabPageById("fzr");
				dw_fzr.setColumn("ZJHM");
				return false;
			}
	}
	if (tp.existTabPageById("ds"))
	{
		var nDSCount=dw_ds.RowCount();
		for( var i=0; i<nDSCount; i++)
		{
			ls_ZJMC=dw_ds.getItemString(i, "ZJMC");
			ls_ZJHM=dw_ds.getItemString(i, "ZJHM");
			if (ls_ZJMC=="01")	//身份证
				if (!validatezjhm(ls_ZJHM))
				{
					tp.selectTabPageById("ds");
					dw_ds.selectRow(i);
					dw_ds2.setColumn("ZJHM");
					return false;
				}
		}
	}
	if (tp.existTabPageById("wt"))
	{
		var nWTCount=dw_wt.RowCount();
		for( var i=0; i<nWTCount; i++)
		{
			ls_ZJMC=dw_wt.getItemString(i, "SFZJ");
			ls_ZJHM=dw_wt.getItemString(i, "ZJHM");
			if (ls_ZJMC=="01")	//身份证
				if (!validatezjhm(ls_ZJHM))
				{
					tp.selectTabPageById("wt");
					dw_wt.selectRow(i);
					dw_wt2.setColumn("ZJHM");
					return false;
				}
		}
	}
	return true;
}

/**
 * 获得新增的意见记录条数 FLAG=0
 *
 */
function getNewYJCount()
{
	var nYJCount=dw_yj.RowCount();
	var nNewYJCount=0;
	for( var i=0; i<nYJCount; i++ )
	{
		if (dw_yj.isValidRow(i) && dw_yj.getItemString(i,"FLAG")=="0")	//有新记录了，不需要添加
		{
			nNewYJCount++;
			break;
		}
	}
	return nNewYJCount;
}


/**
 * 检查审批检查结果，对于强制级别的，如果通过必须输入意见
 */
function validateSPJCJG()
{
	var nJCCount=dw_jc.RowCount();
	for( var i=0; i<nJCCount;i++)
	{
		var JB=dw_jc.getItemString(i, "JB");
		var TG=dw_jc.getItemString(i, "TG");
		var YJ=dw_jc.getItemString(i, "YJ");
		if (dw_jc.isValidRow(i) && JB=="1" && TG=="1" && YJ=="")	//强制锁定，并且允许通过，意见也没有填写
		{
			tp.selectTabPageById("jc");
			dw_jc.selectRow(i);
			dw_jc2.setColumn("YJ");
			alert( "强制级别的检查结果，如果需要通过，请输入意见！");
			return false;
		}
	}
	return true;
}

/**
 * 检查投资双方
 */
function validateTZSF()
{
	if (is_QYLB!="2") return true;
	var ld_ZFCZ=0, ld_WFCZ=0;
	var nTZCount=dw_tz.RowCount();
	for( var i=0; i<nTZCount; i++)
	{
		if (!dw_tz.isValidRow(i)) continue;
		var ls_ZWBZ=dw_tz.getItemString(i,"ZWBZ");
		if ( ls_ZWBZ=="1" )	//中方
		{
			var ls_QYLX=dw_tz.getItemString(i,"QYLX");
			if (ls_QYLX=="")
			{
				tp.selectTabPageById("tz");
				dw_tz.selectRow(i);
				alert("中方投资人必须选择中方企业类型！");
				return false;
			}
			ld_ZFCZ=ld_ZFCZ+dw_tz.getItemFloat(i,"CZMY");
		}
		else
		{
			var ls_QYGB=dw_tz.getItemString(i,"QYGB");
			if (ls_QYGB=="")
			{
				tp.selectTabPageById("tz");
				dw_tz.selectRow(i);
				alert("外方投资人必须选择外方国别！");
				return false;
			}
			ld_WFCZ=ld_WFCZ+dw_tz.getItemFloat(i,"CZMY");
		}
	}
	if (isWZDZ(is_QYLX) && ld_ZFCZ>0)	//外商独资
	{
		tp.selectTabPageById("tz");
		dw_tz.selectRow(i);
		alert( "独资企业不能有中方投资人，请修改！" );
		return false;
	}
	//出资总和与注册资本相比较
	if (CV_isChecked("TZSF",hashCode(""))==-1)	//没有检查过
	{
		var ld_ZCZBMY=dw_jb.getItemFloat(0,"ZCZBMY");
		if (Math.abs(ld_ZCZBMY-ld_WFCZ-ld_ZFCZ)>0)
		{
			var ls_Msg="出资总和不等于注册资本，是否允许通过？\n注册资本（折美元）："+ld_ZCZBMY+"  出资总和（折美元）："+(ld_WFCZ+ld_ZFCZ);
			if (confirm(ls_Msg))
			{
				CV_fillOne( is_BH, "TZSF", hashCode(""), "0", "出资总和不等于注册资本，经允许通过检查。\n注册资本（折美元）："+ld_ZCZBMY+"  出资总和（折美元）："+(ld_WFCZ+ld_ZFCZ) );
				return true;
			}
			else
			{
				tp.selectTabPageById("tz");
				return false;
			}
			return false;
		}
	}
	return true;
}
