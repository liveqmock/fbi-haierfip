
//**************************************************//
//该文件是本系统右健菜单控件文件，任何人不得擅自更改	    //
//若有更改与本人联系：公共包		            //
//**************************************************//

/********************************************************************************
 *
 *      文件名： menu.js
 *
 *      作  用： 右健菜单控件。
 *
 *      作  者： 公共包
 *
 *      时  间：  yyyy-mm-dd
 *
 *      版  权：  leonwoo
 *
 ********************************************************************************/


		var sMI,sMenu;
		var lightTr;
		var showSubMenu = null;
		var oTransContainer = null;
		var frmPanel = null;
		var divPanel = null;
		var tableMenu = null;
		var titleRow = null;
		var titleBar = null;
		var m_innerText ="";

		var m_innerMouseDown = false;
		var m_hostVisible = false;

	    function Menu(menuText){
			menuinit(menuText);
			this.hidden=hide;
			//this.show=show;

		}


		function menuinit(menuText)
		{
			if (menuText.length > 0)
			{
				createInitElements();
				buildMenu(menuText);

			}
		}


		function onElementSelectStart()
		{
			event.returnValue = false;
		}

		function setPanelStyle(obj)
		{
			with (obj.style)
			{
				padding = "0px 0px 0px 0px";
				border = "1px solid #666666";
				margin = "0px 0px 0px 0px";
				color = "menuText";
				fontFamily = "SimSun";
				fontSize = "9pt";
				position = "absolute";
				backgroundColor = "#f9f8f7";
				filter = "progid:DXImageTransform.Microsoft.Shadow(color='dimGray', Direction=135, Strength=2)"
			}
		}

		function createInitElements()
		{


			oTransContainer = document.createElement(
				'<DIV id="MenuContainer"  STYLE="z-index:4998;position:absolute;VISIBILITY:hidden; filter:progid:DXImageTransform.Microsoft.Fade(duration=0.2,overlap=1.0)"></DIV>'
							);
			document.body.appendChild(oTransContainer);

			frmPanel = document.createElement(
				'<iframe frameborder="0" scrolling="no" style="position:absolute;z-index:4999"></iframe>'
							);
			divPanel = document.createElement(
				'<div style="position:absolute;z-index:5000"></div>'
							);
			setPanelStyle(divPanel);

			tableMenu = document.createElement(
				'<table border="0" cellspacing="0" cellpadding="0" style="font-size:12;z-index:5001"></table>'
							);

			titleRow = tableMenu.insertRow();
			titleBar = titleRow.insertCell();

			with (titleBar)
			{
				colSpan = 3;
				style.height = "0px";
				onmousemove = Title_mMove;
				onmousedown = Title_mDown;
				onmouseup = Title_mUp;
			}

			divPanel.appendChild(tableMenu);
			oTransContainer.appendChild(frmPanel);
			oTransContainer.appendChild(divPanel);
		}

		function createImgInTD(strSrc, td)
		{
			var span = document.createElement("span");

			with (span.style)
			{
				filter = "";
				width = "20px";
				height = "16px";
				backgroundImage = "url(" + strSrc + ")";
				backgroundPosition = "center center";
				backgroundRepeat = "no-repeat";
				verticalAlign = "baseline";
			}

			td.appendChild(span);
		}

		function setClsHSpacerStyle(obj)
		{
			with (obj.style)
			{
				paddingLeft = "0px";
				paddingRight = "0px";
				paddingRight = "0px";
				paddingBottom = "2px";
				position = "relative";

				fontSize = "1px";
				margin = "2px 0px 3px 0px";
				overflow = "hidden";

				borderTop = "buttonshadow 1px solid";
				borderBottom = "buttonhighlight 1px solid";
				width = "100%";
				height = "2px";
				fontSize = "1px";
				backgroundColor = "red";
			}
		}

		function createSeperatorTD(td, tr)
		{
			td.style.height="5px";

			td = tr.insertCell();
			td.style.height="5px";
			td.colSpan=3;

			var div = document.createElement("div");

			setClsHSpacerStyle(div);
			td.appendChild(div);
		}

		function clearAllRows(oPart)
		{
			while(oPart.rows.length > 0)
			{
				oPart.deleteRow(0);
			}
		}

		function buildMenu(oMenuItems)
		{
			var arrayItems = oMenuItems;

			if (typeof(oMenuItems) == "string")
				arrayItems = oMenuItems.split(";");

			clearAllRows(tableMenu);

			var i;

			sMI = arrayItems;
			sMenu = new Array(sMI.length);

			for(i = 0; i < sMI.length; i++)
			{
				sMenu[i] = sMI[i].split(",");
			}

			for(i = 0; i < sMenu.length; i++)
			{
				if (sMenu[i][0].length > 0)
				{
					var Tr = tableMenu.insertRow();
					var Td = Tr.insertCell();

					sMenu[i][0] = rTrim(lTrim(sMenu[i][0]));

					Td.innerText = "";
					Td.style.width = "20px";
					Td.vAlign = "middle";

					if (sMenu[i].length > 4)
						if (sMenu[i][4].length > 0)
							createImgInTD(sMenu[i][4], Td);

					Td.style.backgroundColor = "#dbd8d1";

					if(sMenu[i][0] == "-")
						createSeperatorTD(Td, Tr);
					else
					{
						Td.style.height = 20;

						Td = Tr.insertCell();
						Td.vAlign = "middle";

						var span = document.createElement("span");
						span.innerHTML = sMenu[i][0];
						span.style.marginLeft = "4px";
						span.style.cursor = "default";
						Td.appendChild(span);
						Td.span = span;
						Td.noWrap = true;

						if (Td.offsetWidth < 150)
							Td.style.width = 150;

						Td=Tr.insertCell();
						Td.style.width=15;

						if(sMenu[i][1])
						{
							if(element.document.all(sMenu[i][1]))
							{
								Tr.subMenu=element.document.all(sMenu[i][1]);
							}

							Td.innerText = "8";
							Td.style.font = "8pt Marlett";
						}

						Tr.menuText = sMenu[i][0];
						Tr.menuData = lTrim(rTrim(sMenu[i][5]));
						Tr.onmouseenter = mi_OnMouseEnter;
						Tr.onmouseleave = mi_OnMouseLeave;
						Tr.onclick = mi_OnClick;
						Tr.onmousedown = mi_OnMouseDown;
					}
				}	//End For
			}

			setMenuElementAttributes();
		}



		function element_OnDeActivate()
		{
			if (showSubMenu == null && !m_innerMouseDown)
			{
				hide();
			}
			m_innerMouseDown = false;
		}


		function mi_OnMouseEnter()
		{
			var Tr = event.srcElement;

			event.srcElement.style.backgroundColor="#b6bdd2";
			lightTr = event.srcElement;
			tableMenu.setActive();

			if (Tr.subMenu && m_hostVisible)
			{
				showSubMenu = Tr.subMenu;

				var oEvent = createEventObject();

				oEvent.menuData = Tr.menuData;
				oEvent.subMenu = Tr.subMenu;
				oEvent.menuText = Tr.menuText;

				idMenuBeforeShowSubMenu.fire(oEvent);

				showSubMenu.attachEvent("onhidden", onSubMenuHidden);
				showSubMenu.show(absLeft(element) + Tr.offsetLeft + Tr.offsetWidth,
								absTop(element) + Tr.offsetTop);
			}
		}

		function mi_OnMouseLeave()
		{
			event.srcElement.style.backgroundColor = "#f9f8f7";
			event.srcElement.style.color = "menutext";
			lightTr = null;
		}

		function mi_OnClick()
		{
			m_innerMouseDown = true;

			if (lightTr && lightTr.disabled == false)
			{
				hide();
				menuTreeClick(lightTr);




			}
		}

		function mi_OnMouseDown()
		{
			if (lightTr)
			{
				m_innerMouseDown = true;


			}
		}

		function fireBeforePopup(oTR)
		{

			menuTreeBeforePopup(oTR);

			var oResult = new Object;

			oResult.disableItem = oTR.disableItem;
			oResult.returnValue = oTR.returnValue;



			return oResult;
		}

		function browseMenuItem()
		{
			var oResult = null;

			for (var i = 0; i < tableMenu.rows.length; i++)
			{
				var oRow = tableMenu.rows(i);

				oResult = fireBeforePopup(oRow);

				if (oResult.returnValue == false)
					return false;

				oRow.disabled = oResult.disableItem;

				if (typeof(oRow.menuData) != "undefined")
				{
					for (var j = 0; j < oRow.cells.length; j++)
						oRow.cells(j).disabled = oRow.disabled;
				}
			}

			return true;
		}

		function setMenuElementAttributes()
		{
			with(oTransContainer)
			{
				style.left = 0;
				style.top = 0;
				style.width = divPanel.offsetWidth;
				style.height = divPanel.offsetHeight;
			}

			with(divPanel)
			{
				style.left = 0;
				style.top = 0;
				style.width = tableMenu.offsetWidth;
				style.height = tableMenu.offsetHeight;
			}

			with(frmPanel)
			{
				style.left = 0;
				style.top = 0;
				style.width = divPanel.offsetWidth;
				style.height = divPanel.offsetHeight;
			}


		}

		function show(x, y, bNotUseScrollPos)
		{

			m_hostVisible = true;
			m_innerMouseDown = false;

			if (browseMenuItem())
			{

				//applyFilter();

				setMenuElementAttributes();

				var doc = document;
				var nScrollLeft = 0;
				var nScrollTop = 0;

				if (!bNotUseScrollPos)
				{
					nScrollLeft = doc.body.scrollLeft;
					nScrollTop = doc.body.scrollTop;
				}

				with(oTransContainer)
				{
					style.pixelLeft = nScrollLeft +
									Math.min(doc.body.offsetWidth - oTransContainer.offsetWidth, x);
					style.pixelTop = nScrollTop +
									Math.min(doc.body.offsetHeight - oTransContainer.offsetHeight, y);

					style.pixelLeft = Math.max(style.pixelLeft, 0);
					style.pixelTop = Math.max(style.pixelTop, 0);

					style.visibility = "visible";
				}

				//playFilter();

				tableMenu.setActive();
			}
		}

		function isWindows2000()
		{
			var ua = window.navigator.userAgent;

			return ua.indexOf( "Windows NT" ) > 0;
		}

		function applyFilter()
		{

			//if (isWindows2000())
				oTransContainer.filters[0].Apply();


		}

		function playFilter()
		{
			//if (isWindows2000())
				oTransContainer.filters[0].Play();
		}

		function showSubMenu(x, y, father)
		{
			show(x, y);
		}

		function hide()
		{
			m_hostVisible = false;

			oTransContainer.style.visibility = "hidden";


		}

		function isParentElement(parentElem, childElement)
		{
			var elem = childElement;

			while (elem != parentElem && elem)
			{
				elem = elem.parentNode;
			}

			return (elem != null);
		}


		function onSubMenuHidden()
		{
			showSubMenu.detachEvent("onhidden", onSubMenuHidden);

			if (!isParentElement(element, element.document.activeElement))
			{
				hide();
			}

			showSubMenu = null;
		}


		function lTrim(s)
		{
			for (var i = 0; i < s.length; i++)
			{
				if(s.charCodeAt(i) != 32)
					return(s.substr(i));
			}

			return s;
		}

		function rTrim(str)
		{
			var nLen = str.length;
			for (var i = str.length - 1; i >= 0; i--)
				if (str.charCodeAt(i) != 32)
					break;
				else
					nLen--;

			return str.substr(0, nLen);
		}


		var xDown, yDown;
		var xOrg, yOrg;
		var bMoving = false;


		function Title_mDown()
		{
			movingEle = element;
			titleBar.setCapture();

			bMoving=true;

			xDown=event.x;yDown=event.y;

			xOrg=movingEle.style.pixelLeft;
			yOrg=movingEle.style.pixelTop;
		}

		function Title_mUp()
		{
			if (bMoving)
			{
				bMoving = false;
				movingEle = null;
				titleBar.releaseCapture();
			}
		}

		function Title_mMove()
		{
			if (bMoving)
			{
				movingEle.style.position = "absolute";
				var nLeft,nTop;

				nLeft = movingEle.style.pixelLeft+event.x-xDown;
				nTop = movingEle.style.pixelTop+event.y-yDown;

				if (nLeft > 0)
					movingEle.style.pixelLeft = nLeft;
				else
					movingEle.style.pixelLeft = 0;
				if (nTop > 0)
					movingEle.style.pixelTop = nTop;
				else
					movingEle.style.pixelTop = 0;
			}
		}
