/*----------------------------------------------------------------------------\
|                               Tab Pane 1.02                                 |
|-----------------------------------------------------------------------------|
|                         Created by Erik Arvidsson                           |
|                  (http://webfx.eae.net/contact.html#erik)                   |
|                      For WebFX (http://webfx.eae.net/)                      |
|-----------------------------------------------------------------------------|
|                  Copyright (c) 1998 - 2003 Erik Arvidsson                   |
|-----------------------------------------------------------------------------|
| This software is provided "as is", without warranty of any kind, express or |
| implied, including  but not limited  to the warranties of  merchantability, |
| fitness for a particular purpose and noninfringement. In no event shall the |
| authors or  copyright  holders be  liable for any claim,  damages or  other |
| liability, whether  in an  action of  contract, tort  or otherwise, arising |
| from,  out of  or in  connection with  the software or  the  use  or  other |
| dealings in the software.                                                   |
| - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - |
| This  software is  available under the  three different licenses  mentioned |
| below.  To use this software you must chose, and qualify, for one of those. |
| - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - |
| The WebFX Non-Commercial License          http://webfx.eae.net/license.html |
| Permits  anyone the right to use the  software in a  non-commercial context |
| free of charge.                                                             |
| - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - |
| The WebFX Commercial license           http://webfx.eae.net/commercial.html |
| Permits the  license holder the right to use  the software in a  commercial |
| context. Such license must be specifically obtained, however it's valid for |
| any number of  implementations of the licensed software.                    |
| - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - |
| GPL - The GNU General Public License    http://www.gnu.org/licenses/gpl.txt |
| Permits anyone the right to use and modify the software without limitations |
| as long as proper  credits are given  and the original  and modified source |
| code are included. Requires  that the final product, software derivate from |
| the original  source or any  software  utilizing a GPL  component, such  as |
| this, is also licensed under the GPL license.                               |
|-----------------------------------------------------------------------------|
| 2002-01-?? | First working version                                          |
| 2002-02-17 | Cleaned up for 1.0 public version                              |
| 2003-02-18 | Changed from javascript uri for anchors to return false        |
| 2003-03-03 | Added dispose methods to release IE memory                     |
| 2005-01-21 | Added tab disabled flage                                       |
| 2005-01-31 | Refactor selectTabPage, enableTabPage/disableTabPage,          |
|              showTabPage/hideTabPage, getTabPageEnabled, getTabPageVisible, |
|              and those "ById" Function                                      |
| 2005-02-01 | Added get Frame Function 									  |
| 2005-02-01 | Added get/set Height/Width Function                            |
|-----------------------------------------------------------------------------|
| Dependencies: *.css           a css file to define the layout               |
|-----------------------------------------------------------------------------|
| Created  2002-01-?? | All changes are in the log above. | Updated 2003-03-03|
| Modified 2005-01-21 | All changes are in the log above. | Updated 2005-01-21|
| Modified 2005-01-31 | All changes are in the log above. | Updated 2003-01-31|
| Modified 2005-02-01 | All changes are in the log above. | Updated 2003-02-01|
\----------------------------------------------------------------------------*/

// This function is used to define if the browser supports the needed
// features
function hasSupport() {

	if (typeof hasSupport.support != "undefined")
		return hasSupport.support;

	var ie55 = /msie 5\.[56789]/i.test( navigator.userAgent );

	hasSupport.support = ( typeof document.implementation != "undefined" &&
			document.implementation.hasFeature( "html", "1.0" ) || ie55 )

	// IE55 has a serious DOM1 bug... Patch it!
	if ( ie55 ) {
		document._getElementsByTagName = document.getElementsByTagName;
		document.getElementsByTagName = function ( sTagName ) {
			if ( sTagName == "*" )
				return document.all;
			else
				return document._getElementsByTagName( sTagName );
		};
	}

	return hasSupport.support;
};

///////////////////////////////////////////////////////////////////////////////////
// The constructor for tab panes
//
// el : HTMLElement		The html element used to represent the tab pane
// bUseCookie : Boolean	Optional. Default is true. Used to determine whether to us
//						persistance using cookies or not
//
function WebFXTabPane( el, bUseCookie, bView ) {
	if ( !hasSupport() || el == null ) return;

	this.element = el;
	this.element.tabPane = this;
	this.pages = [];
	this.selectedIndex = -1;
	this.useCookie = bUseCookie != null ? bUseCookie : true;
	//2005-01-22 Added by Orchid
	this.paramUrl="";
	this.id=el.id;
	this.view=bView;

	// add class name tag to class name
	this.element.className = this.classNameTag + " " + this.element.className;

	// add tab row
	this.tabRow = document.createElement( "div" );
	this.tabRow.className = "tab-row";
	this.tabRow.style.width="100%";
	el.insertBefore( this.tabRow, el.firstChild );

	// loop through child nodes and add them
	var cs = el.childNodes;
	var n;
	for (var i = 0; i < cs.length; i++) {
		if (cs[i].nodeType == 1 && cs[i].className == "tab-page") {
			this.addTabPage( cs[i] );
		}
	}
	WebFXTabPane.prototype.addTabPage=WebFXTabPane_addTabPage;
	WebFXTabPane.prototype.dispose=WebFXTabPane_dispose;
	WebFXTabPane.prototype.navigate=WebFXTabPane_navigate;
	WebFXTabPane.prototype.saveTabPage=WebFXTabPane_saveTabPage;
	WebFXTabPane.prototype.saveTabPageById=WebFXTabPane_saveTabPageById;
	WebFXTabPane.prototype.getTabPageCount=WebFXTabPane_getTabPageCount;
	WebFXTabPane.prototype.getIndexById=WebFXTabPane_getIndexById;
	WebFXTabPane.prototype.getIdByIndex=WebFXTabPane_getIdByIndex;
	WebFXTabPane.prototype.setParamUrl=WebFXTabPane_setParamUrl;
	WebFXTabPane.prototype.getParamUrl=WebFXTabPane_getParamUrl;
	WebFXTabPane.prototype.existTabPage=WebFXTabPane_existTabPage;
	WebFXTabPane.prototype.existTabPageById=WebFXTabPane_existTabPageById;
	WebFXTabPane.prototype.selectTabPage=WebFXTabPane_selectTabPage;
	WebFXTabPane.prototype.getSelectedIndex=WebFXTabPane_getSelectedIndex;
	WebFXTabPane.prototype.selectTabPageById=WebFXTabPane_selectTabPageById;
	WebFXTabPane.prototype.getSelectedId=WebFXTabPane_getSelectedId;
	WebFXTabPane.prototype.disableTabPage=WebFXTabPane_disableTabPage;
	WebFXTabPane.prototype.enableAllTabPage=WebFXTabPane_enableAllTabPage;
	WebFXTabPane.prototype.enableTabPage=WebFXTabPane_enableTabPage;
	WebFXTabPane.prototype.getTabPageEnabled=WebFXTabPane_getTabPageEnabled;
	WebFXTabPane.prototype.disableTabPageById=WebFXTabPane_disableTabPageById;
	WebFXTabPane.prototype.enableTabPageById=WebFXTabPane_enableTabPageById;
	WebFXTabPane.prototype.getTabPageEnabledById=WebFXTabPane_getTabPageEnabledById;
	WebFXTabPane.prototype.showTabPage=WebFXTabPane_showTabPage;
	WebFXTabPane.prototype.hideTabPage=WebFXTabPane_hideTabPage;
	WebFXTabPane.prototype.getTabPageVisible=WebFXTabPane_getTabPageVisible;
	WebFXTabPane.prototype.showTabPageById=WebFXTabPane_showTabPageById;
	WebFXTabPane.prototype.hideTabPageById=WebFXTabPane_hideTabPageById;
	WebFXTabPane.prototype.getTabPageVisibleById=WebFXTabPane_getTabPageVisibleById;
	WebFXTabPane.prototype.getTabPageFrame=WebFXTabPane_getTabPageFrame;
	WebFXTabPane.prototype.getTabPageFrameById=WebFXTabPane_getTabPageFrameById;
	WebFXTabPane.prototype.setHeight=WebFXTabPane_setHeight;
	WebFXTabPane.prototype.getHeight=WebFXTabPane_getHeight;
	WebFXTabPane.prototype.setWidth=WebFXTabPane_setWidth;
	WebFXTabPane.prototype.getWidth=WebFXTabPane_getWidth;
	WebFXTabPane.prototype.setCookie=WebFXTabPane_setCookie;
	WebFXTabPane.prototype.getCookie=WebFXTabPane_getCookie;
	WebFXTabPane.prototype.removeCookie=WebFXTabPane_removeCookie;
	WebFXTabPane.prototype.getTabPageLoaded=WebFXTabPane_getTabPageLoaded;
	WebFXTabPane.prototype.getTabPageLoadedById=WebFXTabPane_getTabPageLoadedById;
};

WebFXTabPane.prototype.classNameTag = "dynamic-tab-pane-control";


///////////////////////////////////////////////////////////////////////////////////
//  AddTabPage
function WebFXTabPane_addTabPage( oElement ) {
	if ( !hasSupport() ) return;

	if ( oElement.tabPage == this )	// already added
		return oElement.tabPage;

	var n = this.pages.length;
	var tp = this.pages[n] = new WebFXTabPage( oElement, this, n );
	tp.tabPane = this;
	//2005-01-24 Added by Orchid Load URL
	//tp.load();

	// move the tab out of the box
	this.tabRow.appendChild( tp.tab );

	//if ( n == this.selectedIndex )
	//	tp.showContent();
	//else
		tp.hideContent();

	return tp;
};


///////////////////////////////////////////////////////////////////////////////////
//  release Function
function WebFXTabPane_dispose() {
	this.element.tabPane = null;
	this.element = null;
	this.tabRow = null;
	this.selectedIndex = null;
	this.useCookie = null;
	this.paramUrl = null;
	this.id = null;
	this.view = null;

	for (var i = 0; i < this.pages.length; i++) {
		this.pages[i].dispose();
		this.pages[i] = null;
	}
	this.pages = null;

	this.addTabPage = null;
	this.navigate = null;
	this.saveTabPage = null;
	this.saveTabPageById = null;
	this.getTabPageCount = null;
	this.getIndexById = null;
	this.getIdByIndex = null;
	this.setParamUrl = null;
	this.getParamUrl = null;
	this.existTabPage = null;
	this.existTabPageById = null;
	this.selectTabPage = null;
	this.getSelectedIndex = null;
	this.selectTabPageById = null;
	this.getSelectedId = null;
	this.disableTabPage = null;
	this.enableAllTabPage = null;
	this.enableTabPage = null;
	this.getTabPageEnabled = null;
	this.disableTabPageById = null;
	this.enableTabPageById = null;
	this.getTabPageEnabledById = null;
	this.showTabPage = null;
	this.hideTabPage = null;
	this.getTabPageVisible = null;
	this.showTabPageById = null;
	this.hideTabPageById = null;
	this.getTabPageVisibleById = null;
	this.getTabPageFrame = null;
	this.getTabPageFrameById = null;
	this.setHeight = null;
	this.getHeight = null;
	this.setWidth = null;
	this.getWidth = null;
	this.setCookie = null;
	this.getCookie = null;
	this.removeCookie = null;
	this.getTabPageLoaded = null;
	this.getTabPageLoadedById = null;
	this.dispose = null;
};


///////////////////////////////////////////////////////////////////////////////////
//   Naviage
//2005-01-22 Added by Orchid
// dir is forward or backward
function WebFXTabPane_navigate( dir ) {
	var count=this.pages.length;
	var nextIndex=this.selectedIndex;
	if (dir=="backward")
	{
		for ( i=this.selectedIndex-1; i>=0 ;i-- )
		{
			if (this.pages[i].enabled)
			{
				nextIndex=i;
				break;
			}
		}
	}
	else
	{
		for( i=this.selectedIndex+1; i<count; i++ )
		{
			if (this.pages[i].enabled)
			{
				nextIndex=i;
				break;
			}
		}
	}
	this.selectTabPage( nextIndex );
};


///////////////////////////////////////////////////////////////////////////////////
//  Save Page
//2005-01-22 Added by Orchid
function WebFXTabPane_saveTabPage( n ) {
	var count=this.pages.length;
	if (n<0 || n>count ) return;
	return this.pages[ n ].save();
};

//2005-01-28 Added by Orchid
function WebFXTabPane_saveTabPageById( Id ) {
	var n=this.getIndexById( Id );
	return this.saveTabPage( n );
};

//2005-01-22 Added by Orchid
//获得TabPage的数量
function WebFXTabPane_getTabPageCount() {
	return this.pages.length;
};



///////////////////////////////////////////////////////////////////////////////////
//  Id & Index Conversion
//2005-01-28 Added By Orchid
function WebFXTabPane_getIndexById( Id ) {
	var count=this.pages.length;
	var nIndex=-1;
	for( i=0; i<count; i++ )
	{
		if ( this.pages[i].id==Id)
		{
			nIndex=i;
			break;
		}
	}
	return nIndex;
};

function WebFXTabPane_getIdByIndex( n ) {
	var count=this.pages.length;
	if (n<0 || n>count )
		return "";
	return this.pages[n].id;
};
///////////////////////////////////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////
//  ParamUrl Function
//2005-01-22 Added by Orchid
function WebFXTabPane_setParamUrl( url ) {
	this.paramUrl=url;
}

function WebFXTabPane_getParamUrl() {
	return this.paramUrl;
}
///////////////////////////////////////////////////////////////////////////////////



///////////////////////////////////////////////////////////////////////////////////
//  Exist Page Function
//  Index Function
//2005-01-22 Added by Orchid
//判断给定序号的Tab页是否存在
function WebFXTabPane_existTabPage( n ) {
	if ( n>=0 && n<this.pages.length )
		return true;
	else
		return false;
};

//  Id Function
//2005-01-22 Added by Orchid
//判断给定ID的TAB页是否存在。
function WebFXTabPane_existTabPageById( Id ) {
	var n=this.getIndexById( Id );
	if (n<0)
		return false;
	else
		return true;
};
///////////////////////////////////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////
//  Select, SelectIndex Function
//  Index Function
function WebFXTabPane_selectTabPage( n ) {
    //alert(n);
    //alert(this.selectedIndex);
	if (this.selectedIndex != n)
	{
		//已经选择的页面存在选择的
		if (this.selectedIndex != null && this.pages[ this.selectedIndex ] != null )
		{
			//判断该页面能否失去焦点
			var funcName="TabPane_"+this.id+"_loseFocus";
			if (eval("typeof("+funcName+")==\"function\";"))	//函数存在
				if (!eval(funcName+"('"+this.pages[ this.selectedIndex ].id+"')"))
					return;
			if (this.pages[this.selectedIndex].isModified()
			&& this.pages[this.selectedIndex].autoUnload=="true" )	//已经修改并且该页将要被卸载
			{
				alert( "当前页数据已经修改，请先保存再切换！" );
				return;
			}
			this.pages[ this.selectedIndex ].hideContent();
			if (this.pages[ this.selectedIndex ].autoUnload=="true" )	//页面自动卸载的情况下，卸载页面
				this.pages[ this.selectedIndex ].unload();
			//	去掉loseFose（）消除tab页关联，页面失去焦点bug	
			// date:2006-2-10
			// edited by leonwoo 		
			//this.pages[ this.selectedIndex].loseFocus();
		}
		//将要选择的页面存在
		if (n!=null && this.pages[ n ] != null )
		{
			this.selectedIndex = n;
			this.pages[ this.selectedIndex ].showContent();
			if (this.pages[ this.selectedIndex ].autoUnload=="true" )	//页面自动卸载的情况下，切换后装载页面
				this.pages[ this.selectedIndex ].load();

			//获得焦点之后执行一个功能
			var funcName="TabPane_"+this.id+"_getFocus";
			if (eval("typeof("+funcName+")==\"function\";")){	//函数存在
				//alert(this.pages[this.selectedIndex].id);
				if (!eval(funcName+"('"+this.pages[this.selectedIndex].id+"')"))
					return;
			}
			
			if ( this.useCookie )
				WebFXTabPane.setCookie( "webfxtab_" + this.element.id, n );	// session cookie
		}
		//window.focus();
		//this.pages[ this.selectedIndex].getFocus()
	}
};


//2005-01-22 Added by Orchid
function WebFXTabPane_getSelectedIndex() {
	return this.selectedIndex;
};

//  Id Function
//2005-01-21 Added by Orchid
//按照ID选择Tab页
function WebFXTabPane_selectTabPageById( Id ) {
	var n=this.getIndexById( Id );
	if (n<0) return;
	this.selectTabPage( n );
};

//2005-01-22 Added by Orchid
function WebFXTabPane_getSelectedId() {
	return this.pages[ this.selectedIndex ].id;
};
///////////////////////////////////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////
//  Enable, Disable, Enabled Function
//  Index Function
//2005-01-21 Added by Orchid
function WebFXTabPane_disableTabPage( n ) {
	if (n>=0 && n<this.pages.length)
	{
		this.pages[n].disable();
	}
};

//2005-04-24 Added By Orchid
function WebFXTabPane_enableAllTabPage()
{
	document.getElementById(this.id+"__message").style.visibility="hidden";
	var nPages=this.pages.length;
	for( var i=0; i<nPages; i++)
	{
		this.pages[i].enable();
	}
	var tabIndex = 0;
	if ( this.useCookie ) {
		tabIndex = Number( WebFXTabPane.getCookie( "webfxtab_" + this.element.id ) );
		if ( isNaN( tabIndex ) )
			tabIndex = 0;
	}
	this.selectTabPage( tabIndex );
}

//2005-01-21 Added by Orchid
function WebFXTabPane_enableTabPage( n ) {
	if (n>=0 && n<this.pages.length)
	{
		this.pages[n].enable();
	}
};

//2005-01-28 Added By Orchid
function WebFXTabPane_getTabPageEnabled( n ) {
	var count=this.pages.length;
	if (n<0 || n>count ) return false;
	return this.pages[n].enabled;
};


//  Id Function
//2005-01-28 Added By Orchid
function WebFXTabPane_disableTabPageById( Id ) {
	var n=this.getIndexById( Id );
	if (n<0) return;
	this.disableTabPage( n );
};


//2005-01-21 Added by Orchid
function WebFXTabPane_enableTabPageById( Id ) {
	var n=this.getIndexById( Id );
	if (n<0) return;
	this.enableTabPage( n );
};



function WebFXTabPane_getTabPageEnabledById( Id ) {
	var n=this.getIndexById( Id );
	return this.getTabPageEnabled( n );
};
///////////////////////////////////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////
//	Show, Hide, Visible Function
//  Index Function
//2005-01-28 Added By Orchid
function WebFXTabPane_showTabPage( n ) {
	this.pages[n].setVisible(true);
};

//2005-01-28 Added By Orchid
function WebFXTabPane_hideTabPage( n ) {
	this.pages[n].setVisible(false);
};

//2005-01-28 Added By Orchid
function WebFXTabPane_getTabPageVisible( n ) {
	var count=this.pages.length;
	if (n<0 || n>count ) return false;
	return this.pages[n].visible;
};

//  Id Function
//2005-01-28 Added By Orchid
function WebFXTabPane_showTabPageById( Id ) {
	var n=this.getIndexById( Id );
	if (n<0) return;
	this.showTabPage( n );
};

//2005-01-28 Added By Orchid
function WebFXTabPane_hideTabPageById( Id ) {
	var n=this.getIndexById( Id );
	if (n<0) return;
	this.hideTabPage( n );
};

//2005-01-28 Added By Orchid
function WebFXTabPane_getTabPageVisibleById( Id ) {
	var n=this.getIndexById( Id );
	return this.getTabPageVisible( n );
};
///////////////////////////////////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////
//2005-02-01 Added By Orchid
//获得TabPage 中的Frame
function WebFXTabPane_getTabPageFrame( n ) {
	var count=this.pages.length;
	if (n<0 || n>count )
		return null;
	return eval( "document.frame_"+this.pages[n].id );
};

function WebFXTabPane_getTabPageFrameById( Id ) {
	var count=this.pages.length;
	var n=this.getIndexById( Id );
	if (n<0 || n>count )
		return null;
	return eval( "document.frame_"+this.pages[n].id );
};
///////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////
//2005-02-01 Added By Orchid
//
function WebFXTabPane_setHeight( height ) {
	eval( this.id+"Container.style.height='"+height+"'" );
	var nHeight=this.getHeight();
	eval( this.id+"ContentContainer.style.height="+(nHeight-20) );
};

function WebFXTabPane_getHeight() {
	return eval( this.id+"Container.offsetHeight" );
};


function WebFXTabPane_setWidth( width ) {
	eval( this.id+"Container.style.width='"+width+"'" );
	eval( this.id+"ContentContainer.style.width='100%'" );
};

function WebFXTabPane_getWidth() {
	return eval( this.id+"Container.offsetWidth" );
};
///////////////////////////////////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////
//  TabPage Load Status Function
//  Added by Orchid 2005.02.19
function WebFXTabPane_getTabPageLoaded( n ) {
	page=this.getTabPageFrame( n );
	if (page==null)	return true;
	var doc=page.document;
	if (doc.readyState!="complete")
	{
		return false;
	}
	//log( n + ":1" + page  );
	var body=doc.body;
	//log( n + ":2" + body );
	if (body==null)
	{
		return false;
	}
	//log( n + ":3" + body );
	//log( n+ ":4" + body.getAttribute("LOADED") );
	//alert( "Hello" );
	if (body.getAttribute("LOADED")==undefined ||
		body.getAttribute("LOADED")==null ||
		body.getAttribute("LOADED")=="" )
	{
		return false;
	}
	if (body.getAttribute("LOADED").value=="false")
	{
		return false;
	}
	return true;
};

function WebFXTabPane_getTabPageLoadedById( Id ) {
	var n=this.getIndexById( Id );
	if (n<0) return true;
	return this.getTabPageLoaded( n );
};
///////////////////////////////////////////////////////////////////////////////////



///////////////////////////////////////////////////////////////////////////////////
//  Misc Function: Cookie Function
// Cookie handling
function WebFXTabPane_setCookie( sName, sValue, nDays ) {
	var expires = "";
	if ( nDays ) {
		var d = new Date();
		d.setTime( d.getTime() + nDays * 24 * 60 * 60 * 1000 );
		expires = "; expires=" + d.toGMTString();
	}

	document.cookie = sName + "=" + sValue + expires + "; path=/";
};

function WebFXTabPane_getCookie(sName) {
	var re = new RegExp( "(\;|^)[^;]*(" + sName + ")\=([^;]*)(;|$)" );
	var res = re.exec( document.cookie );
	return res != null ? res[3] : null;
};

function WebFXTabPane_removeCookie( name ) {
	setCookie( name, "", -1 );
};

///////////////////////////////////////////////////////////////////////////////////







///////////////////////////////////////////////////////////////////////////////////
// The constructor for tab pages. This one should not be used.
// Use WebFXTabPage.addTabPage instead
//
// el : HTMLElement			The html element used to represent the tab pane
// tabPane : WebFXTabPane	The parent tab pane
// nindex :	Number			The index of the page in the parent pane page array
//

// TabPage拥有如下属性：
//  useIFrame( "true", "false" ):缺省"true",使用IFrame显示页面
//  initLoad( "true", "false" ):缺省"false",不在创建的时候加载页面
//  autoUnload( "true", "false" ):缺省"true",页面切换的时候自动卸载页面
//
function WebFXTabPage( el, tabPane, nIndex ) {
	if ( !hasSupport() || el == null ) return;

	this.element = el;
	this.element.tabPage = this;
	this.index = nIndex;
	//2005-01-21 Modified by Orchid
	this.enabled=true;
	this.id=el.id;
	this.url=el.url;
	this.visible=true;

	if (this.url==null || this.url==undefined)
	{
		this.url="";
	}
	//使用IFrame调度页面，缺省使用IFrame调度页面
	this.useIFrame=el.useIFrame;
	if (this.useIFrame==null || this.useIFrame==undefined)
	{
		this.useIFrame="true";
	}

	//页面加载标志，缺省未加载
	this.loaded=false;
	//自动加载页面的内容，缺省不自动加载
	this.initLoad=el.initLoad;
	if (this.initLoad==null || this.initLoad==undefined)
	{
		this.initLoad="false";
	}

	//自动卸载当前页面内容，缺省自动卸载
	this.autoUnload=el.autoUnload;
	if (this.autoUnload==null || this.autoUnload==undefined)
	{
		this.autoUnload="true";
	}

	var cs = el.childNodes;
	for (var i = 0; i < cs.length; i++) {
		if (cs[i].nodeType == 1 && cs[i].className == "tab") {
			this.tab = cs[i];
			//this.id=el.id;
			break;
		}
	}

	// insert a tag around content to support keyboard navigation

	var a = document.createElement( "A" );
	this.aElement = a;
	a.href = "#";
	a.onclick = function () { return false; };
	while ( this.tab.hasChildNodes() )
		a.appendChild( this.tab.firstChild );
	this.tab.appendChild( a );

	// hook up events, using DOM0
	var oThis = this;
	this.tab.onclick = function ()
	{
		oThis.select(event);
	};
	this.tab.onmouseover = function () { WebFXTabPage.tabOver( oThis ); };
	this.tab.onmouseout = function () { WebFXTabPage.tabOut( oThis ); };
};
WebFXTabPage.prototype.setVisible = WebFXTabPage_setVisible;
WebFXTabPage.prototype.showContent = WebFXTabPage_showContent;
WebFXTabPage.prototype.hideContent = WebFXTabPage_hideContent;
WebFXTabPage.prototype.select = WebFXTabPage_select;
WebFXTabPage.prototype.disable = WebFXTabPage_disable;
WebFXTabPage.prototype.enable = WebFXTabPage_enable;
WebFXTabPage.prototype.isModified = WebFXTabPage_isModified;
WebFXTabPage.prototype.save = WebFXTabPage_save;
WebFXTabPage.prototype.load = WebFXTabPage_load;
WebFXTabPage.prototype.unload = WebFXTabPage_unload;
WebFXTabPage.prototype.loseFocus = WebFXTabPage_loseFocus;
WebFXTabPage.prototype.getFocus = WebFXTabPage_getFocus;

WebFXTabPage.prototype.dispose = WebFXTabPage_dispose;
WebFXTabPage.tabOver=WebFXTabPage_tabOver;
WebFXTabPage.tabOut=WebFXTabPage_tabOut;

//2005-01-28 Added by Orchid
function WebFXTabPage_setVisible( isVisible ) {
	this.visible = isVisible;
	var el = this.tab;
	if (this.visible)
		el.style.display = "block";
	else
		el.style.display = "none";
}

function WebFXTabPage_showContent() {
	var el = this.tab;
	var s = el.className + " selected";
	s = s.replace(/ +/g, " ");
	el.className = s;
	//this.element.style.display="block";

	this.element.style.visibility="visible";
};

function WebFXTabPage_hideContent() {
	var el = this.tab;
	var s = el.className;
	s = s.replace(/ selected/g, "");
	el.className = s;
	//this.element.style.display="none";
	this.element.style.visibility="hidden";
};

function WebFXTabPage_select(e) {
	//2005-01-21 Modified by Orchid
	if (this.enabled==false && (e.ctrlKey==false || e.altKey==false))	//TabPage is disabled
		return;
	this.tabPane.selectTabPage( this.index );
};

//2005-01-21 Added by Orchid
function WebFXTabPage_disable() {
	var el = this.tab;
	var s = el.className + " disabled";
	s = s.replace(/ +/g, " ");
	el.className = s;
	this.enabled=false;
};

//2005-01-21 Added by Orchid
function WebFXTabPage_enable() {
	var el = this.tab;
	var s = el.className;
	s = s.replace(/ disabled/g, "");
	el.className = s;

	this.enabled=true;
};

//2005-01-22 Added by Orchid
//判断当前Tab页中的Frame内嵌入的页面是否已经修改
//用于Tab页切换时检查调用
function WebFXTabPage_isModified()
{
	if (this.useIFrame=="true")	//只有使用IFrame的情况下才做这种判断
	{
		var funcName="document.frame_"+this.id+".isModified";
		if ( eval("(typeof("+funcName+")==\"function\");") )
		{
			return eval( "document.frame_"+this.id+".isModified()");
		}
		else
			return false;	//没有修改函数判断的情况下，缺省作为没有修改
	}
	else	//不使用IFrame的情况，缺省作为没有修改
		return false;
};

//2005-01-22 Added by Orchid
function WebFXTabPage_save()
{
	//使用IFrame的时候，使用本函数保存
	if (this.useIFrame=="true")
	{
		var funcName="document.frame_"+this.id+".save";
		if (eval("typeof("+funcName+")==\"function\";"))	//函数存在
		{
			return eval( "document.frame_"+this.id+".save();" );
		}
		else
			alert( "当前页面存盘函数没有定义！" );
	}
	else
		alert( "不使用IFrame的情况下，本函数无效！" );
}

//2005-01-22 Added by Orchid
//load url
function WebFXTabPage_load() {
	if (!this.loaded)	//没有加载页面，加载页面
	{
		var urlNew=this.url;
		if (urlNew.indexOf("?")==-1)	//不包含?
			urlNew=urlNew+"?"+this.tabPane.paramUrl;
		else
			urlNew=urlNew+"&"+this.tabPane.paramUrl;

		//alert( urlNew );

		eval( "document.all.frame_"+this.id+".src=\""+urlNew+"\";" );
		this.loaded=true;
	}
};

function WebFXTabPage_unload() {
	if (this.loaded)	//已经加载页面，撤销页面
	{
		eval( "document.all.frame_"+this.id+".src=\"about:blank\"" );
		this.loaded=false;
	}
};

//2005-03-12 新增，页面切换后让隐藏页失去焦点
function WebFXTabPage_loseFocus() {
	var winObj=eval( "document.all.frame_"+this.id );
	var docObj=winObj.document;
	if (docObj.activeElement!=undefined && docObj.activeElement!=null)
		docObj.activeElement.blur();
};

function WebFXTabPage_getFocus() {
	//下面的代码没有效果
	
/*	var winObj=eval( "document.all.frame_"+this.id );
	winObj.focus();
	var docObj=winObj.document;
	docObj.focus();
	if (docObj.activeElement!=undefined && docObj.activeElement!=null)
		docObj.activeElement.focus();*/
};



function WebFXTabPage_dispose() {
	this.aElement.onclick = null;
	this.aElement = null;
	this.element.tabPage = null;
	this.tab.onclick = null;
	this.tab.onmouseover = null;
	this.tab.onmouseout = null;
	this.tab = null;
	this.tabPane = null;
	this.element = null;
	this.enabled = null;
	this.index = null;
	this.id = null;
	this.url = null;
	this.visible = null;
	this.useIFrame = null;
	this.loaded = null;
	this.initLoad = null;
	this.autoUnload = null;

	this.setVisible = null;
	this.showContent = null;
	this.hideContent = null;
	this.select = null;
	this.disable = null;
	this.enable = null;
	this.isModified = null;
	this.save = null;
	this.load = null;
	this.unload = null;
	this.loseFocus = null;
	this.getFocus = null;

	this.dispose = null;
	WebFXTabPage.tabOver=null;
	WebFXTabPage.tabOut=null;
	oThis = null;
	//alert( "dispose page" );
};

function WebFXTabPage_tabOver( tabpage ) {
	//2005-01-21 Modified by Orchid
	if (tabpage.enabled==true)
	{
		var el = tabpage.tab;
		var s = el.className + " hover";
		s = s.replace(/ +/g, " ");
		el.className = s;
	}
};

function WebFXTabPage_tabOut( tabpage ) {
	//2005-01-21 Modified by Orchid
	if (tabpage.enabled==true)
	{
		var el = tabpage.tab;
		var s = el.className;
		s = s.replace(/ hover/g, "");
		el.className = s;
	}
};



// This function initializes all uninitialized tab panes and tab pages
function setupAllTabs() {
	if ( !hasSupport() ) return;

	var all = document.getElementsByTagName( "div" );
	var l = all.length;
	var tabPaneRe = /tab\-pane/;
	var tabPageRe = /tab\-page/;
	var cn, el;
	var parentTabPane;

	for ( var i = 0; i < 3*l; i++ ) {
		el = all[i];
		if (el==undefined || el==null ) break;
		cn = el.className;

		// no className
		if ( cn == "" ) continue;
		// uninitiated tab pane
		if ( tabPaneRe.test( cn ) && !el.tabPane )
			new WebFXTabPane( el );

		// unitiated tab page wit a valid tab pane parent
		else if ( tabPageRe.test( cn ) && !el.tabPage &&
					tabPaneRe.test( el.parentNode.className ) ) {
			el.parentNode.tabPane.addTabPage( el );
		}
		//2005-01-24 Added by Orchid
		//inited tab page when initLoad=="true"
		else if ( tabPageRe.test( cn ) && el.tabPage)
		{
			if (el.tabPage.tabPane.view==undefined || !el.tabPage.tabPane.view)
				el.tabPage.disable();
			if (el.tabPage.initLoad=="true")
			{
				el.tabPage.load();
			}
		}
		//inited tagPane, init selectedIndex page
		else if (tabPaneRe.test( cn ) && el.tabPane )
		{
			if (el.tabPane.view)
			{
				document.getElementById(el.tabPane.id+"__message").style.visibility="hidden";
				var tabIndex = 0;
				if ( el.tabPane.useCookie ) {
					tabIndex = Number( WebFXTabPane.getCookie( "webfxtab_" + el.tabPane.element.id ) );
					if ( isNaN( tabIndex ) )
						tabIndex = 0;
				}
				el.tabPane.selectTabPage( tabIndex );
			}
		}
	}
	setupTabPageSize();
};

function setupTabPageSize()
{
	if ( !hasSupport() ) return;

	var all = document.getElementsByTagName( "*" );
	var l = all.length;
	var tabPageRe = /tab\-page/;
	var cn, el;
	var nHeight=0, nWidth=0;

	for ( var i = 0; i < l; i++ )
	{
		el = all[i];
		cn = el.className;
		if ( cn == "" ) continue; // no className
		if ( tabPageRe.test( cn ) && el.tabPage)
		{
			var id=el.tabPage.tabPane.id;
			nHeight=document.getElementById(id+"ContentContainer").offsetHeight;
			nWidth =document.getElementById(id+"ContentContainer").offsetWidth;
			el.style.height=nHeight;
			el.style.width =nWidth;
		}
	}
};

function disposeAllTabs() {
	if ( !hasSupport() ) return;

	var all = document.getElementsByTagName( "*" );
	var l = all.length;
	var tabPaneRe = /tab\-pane/;
	var cn, el;
	var tabPanes = [];

	for ( var i = 0; i < l; i++ ) {
		el = all[i]
		cn = el.className;

		// no className
		if ( cn == "" ) continue;

		// tab pane
		if ( tabPaneRe.test( cn ) && el.tabPane )
			tabPanes[tabPanes.length] = el.tabPane;
	}

	for (var i = tabPanes.length - 1; i >= 0; i--) {
		tabPanes[i].dispose();
		tabPanes[i] = null;
	}
};


// initialization hook up

// DOM2
if ( typeof window.addEventListener != "undefined" )
{
	window.addEventListener( "load", setupAllTabs, false );
	window.addEventListener( "resize", setupTabPageSize, false );
}

// IE
else if ( typeof window.attachEvent != "undefined" )
{
	window.attachEvent( "onload", setupAllTabs );
	window.attachEvent( "onunload", disposeAllTabs );
	window.attachEvent( "onresize", setupTabPageSize );
}

else {
	if ( window.onload != null ) {
		var oldOnload = window.onload;
		window.onload = function ( e ) {
			oldOnload( e );
			setupAllTabs();
		};
	}
	else
		window.onload = setupAllTabs;
}