function enableTagpage( srcElement, enablepage )
{
    srcElement.style.position = "relative";
    srcElement.style.top = "2px";
    srcElement.style.left = "0px";
    srcElement.style.backgroundColor = "#D4D0C8";
    enablepage.style.visibility = "visible";
}

function disableTagpage( srcElement, disablepage )
{
    srcElement.style.top = "0px";
    srcElement.style.left = "0px";
    srcElement.style.backgroundColor = "#D4D0C8";
    disablepage.style.visibility = "hidden";
}

function TagContainer( rootEle )
{
    TagContainer.prototype.caption = "";
    TagContainer.prototype.positionX = 20;
    TagContainer.prototype.positionY = 20;
    TagContainer.prototype.height = 450;
    TagContainer.prototype.width = 960;
    TagContainer.prototype.ifwidth = 935;
    TagContainer.prototype.ifheight = 425;
    TagContainer.prototype.pageIdPrefix = "tagpage";
    TagContainer.prototype.frameIdPrefix = "iframe";
    TagContainer.prototype.ifplus = 25;
    TagContainer.prototype.tagCount = 0;
    TagContainer.prototype.activeTag = -1;
    TagContainer.prototype.container = new Array();
    TagContainer.prototype.rootEle = rootEle;
    TagContainer.prototype.createTag = TagContainer_createTag;
    TagContainer.prototype.addTag = TagContainer_addTag;
    TagContainer.prototype.hide = TagContainer_hideTag;
    TagContainer.prototype.show = TagContainer_showTag;
    TagContainer.prototype.active = TagContainer_activeTag;
    TagContainer.prototype.getTagFrame = TagContainer_getTagFrame;
    p_InitTag( rootEle, 20, 20, 960, 450 );
}

function TagContainer( rootEle, lPositionX, lPositionY, lWidth, lHeight, lPageIdPrefix, lFrameIdPrefix )
{
    TagContainer.prototype.ifplus = 25;
    TagContainer.prototype.caption = "";
    TagContainer.prototype.positionX = lPositionX;
    TagContainer.prototype.positionY = lPositionY;
    TagContainer.prototype.height = lWidth;
    TagContainer.prototype.width = lHeight;
    TagContainer.prototype.ifwidth = lWidth - this.ifplus;
    TagContainer.prototype.ifheight = lHeight - this.ifplus;
    TagContainer.prototype.pageIdPrefix = lPageIdPrefix;
    TagContainer.prototype.frameIdPrefix = lFrameIdPrefix;
    TagContainer.prototype.tagCount = 0;
    TagContainer.prototype.activeTag = -1;
    TagContainer.prototype.container = new Array();
    TagContainer.prototype.rootEle = rootEle;
    TagContainer.prototype.createTag = TagContainer_createTag;
    TagContainer.prototype.addTag = TagContainer_addTag;
    TagContainer.prototype.tagClick = p_tagclick;
    TagContainer.prototype.hide = TagContainer_hideTag;
    TagContainer.prototype.show = TagContainer_showTag;
    TagContainer.prototype.active = TagContainer_activeTag;
    TagContainer.prototype.getTagFrame = TagContainer_getTagFrame;
    p_InitTag( rootEle, lPositionX, lPositionY, lWidth, lHeight );
}

function p_tagclick()
{
    if ( p_operateTag( event.srcElement.getAttribute( "id" ) * 1, event.srcElement.fromParent,
                       event.srcElement.fromParent.activeTag ) )
    {
        event.srcElement.fromParent.activeTag = event.srcElement.getAttribute( "id" );
    }
}

function TagContainer_hideTag( index )
{
    var tag = this.container[ "" + index ];

    if ( !tag.visible )
    {
        return false;
    }

    tag.setInvisible();
    var tagHeader = document.getElementById( "" + index );
    var headerC = document.getElementById( "tagheadercontainer" );
    headerC.removeChild( tagHeader );
    var tagPage = document.getElementById( this.pageIdPrefix + index );
    var pageC = document.getElementById( "tagpagecontainer" );
    pageC.removeChild( tagPage );
}

function TagContainer_showTag( index )
{
    var tag = this.container[ "" + index ];

    if ( tag == undefined ) return;

    if ( tag.visible ) return;

    tag.setVisible();
    var tagHeader = document.createElement( "div" );
    tagHeader.setAttribute( "id", tag.id );
    tagHeader.onclick = this.tagClick;
    tagHeader.fromParent = this;
    tagHeader.innerText = tag.header;
    p_InactiveTag ( tagHeader );
    var nextElement;
    var i = index;

    if ( index * 1 == this.tagCount )
    {
        document.getElementById( "tagheaderContainer" ).appendChild( tagHeader );
    }
    else
    {
        for (;i < ( this.tagCount + 1 ); i++)
        {
            var tmp = document.getElementById( "" + i );

            if ( tmp != null || tmp != undefined )
            {
                nextElement = tmp;
                break;
            }
        }

        if ( i == ( this.tagCount * 1 + 1 ) )
        {
            document.getElementById( "tagheaderContainer" ).appendChild( tagHeader );
        }
        else
        {
            document.getElementById( "tagheaderContainer" ).insertBefore( tagHeader, nextElement );
        }
    }

    var tagPageId = this.pageIdPrefix + index;
    var tagPage = document.createElement( "div" );
    tagPage.setAttribute( "id", tagPageId );
    tagPage.style.position = "absolute";
    tagPage.style.top = "10px";
    tagPage.style.left = "10px";
    tagPage.style.zIndex = "" + index;
    tagPage.style.visibility = "hidden";

    if ( index * 1 == this.tagCount )
    {
        document.getElementById( "tagpageContainer" ).appendChild( tagPage );
    }
    else
    {
        if ( i == ( this.tagCount * 1 + 1 ) )
        {
            document.getElementById( "tagpageContainer" ).appendChild( tagPage );
        }
        else
        {
            nextElement = document.getElementById( this.pageIdPrefix + "" + i );
            document.getElementById( "tagpageContainer" ).insertBefore( tagPage, nextElement );
        }
    }

    var pageframeId = this.frameIdPrefix + index;
    var tagPageframe = document.createElement( "iframe" );
    tagPageframe.setAttribute( "id", pageframeId );
    tagPageframe.setAttribute( "src", tag.url );
    tagPageframe.style.position = "relative";
    tagPageframe.style.top = "0px";
    tagPageframe.style.left = "0px";
    tagPageframe.frameBorder = "no";
    tagPageframe.hspace = "0";
    tagPageframe.vspace = "0";
    tagPageframe.align = "center";
    tagPageframe.setAttribute( "width", this.ifwidth + "px" );
    tagPageframe.setAttribute( "height", this.ifheight + "px" );
    document.getElementById( tagPageId ).appendChild( tagPageframe );
}

function TagContainer_activeTag( index )
{
    if ( p_operateTag( "" + index, this, this.activeTag ) )
    {
        this.activeTag = index;
    }
}

function p_operateTag( headerId, tagContainer, activeTag )
{
    var ttag = tagContainer.container[ "" + activeTag ];

    if ( !ttag.onTagBlur() )
    {
        return false;
    }

    for (var i = 1; i < ( tagContainer.tagCount * 1 + 1 ); i++)
    {
        var tag = tagContainer.container[ "" + i ];

        if ( tag.visible )
        {
            var elementId = tagContainer.pageIdPrefix + i;

            if ( i != headerId )
            {
                disableTagpage( document.getElementById( "" + i ), document.getElementById( elementId ) );
            }
            else
            {
                var nextTag = tagContainer.container[ "" + i ];
                nextTag.onTagFocus();
                enableTagpage( document.getElementById( "" + i ), document.getElementById( elementId ) );
            }
        }
    }

    return true;
}

function TagContainer_getTagFrame( index )
{
    return document.frames( this.frameIdPrefix + "" + index );
}

function TagContainer_setCaption( lCaption )
{
    this.caption = lCaption;
}

function TagContainer_setPositionX( lPositionX )
{
    this.positionX = lPositionX;
}

function TagContainer_setPositionY( lPositionY )
{
    this.positionY = lPositionY;
}

function TagContainer_setWidth( lWidth )
{
    this.width = lWidth;
    this.ifwidth = lWidth - this.ifplus;
}

function TagContainer_setHeight( lHeight )
{
    this.height = lHeight;
    this.ifheight = lHeight - this.ifplus;
}

function TagContainer_setPageIdPrefix( lPageIdPrefix )
{
    this.pageIdPrefix = lPageIdPrefix;
}

function TagContainer_setFrameIdPrefix( lFrameIdPrefix )
{
    this.frameIdPrefix = lFrameIdPrefix;
}

function TagContainer_addTag( tag, lActiveTag )
{
    this.tagCount = ( this.tagCount ) * 1 + 1;
    this.container[ "" + this.tagCount ] = tag;
    var tagHeader = document.createElement( "div" );
    tagHeader.setAttribute( "id", tag.id );
    tagHeader.onclick = this.tagClick;
    tagHeader.fromParent = this;
    tagHeader.innerText = tag.header;

    if ( lActiveTag )
    {
        p_ActiveTag ( tagHeader );
        this.activeTag = this.tagCount;
    }
    else
    {
        p_InactiveTag ( tagHeader );
    }

    document.getElementById( "tagheaderContainer" ).appendChild( tagHeader );
    var tagPageId = this.pageIdPrefix + this.tagCount;
    var tagPage = document.createElement( "div" );
    tagPage.setAttribute( "id", tagPageId );
    tagPage.style.position = "absolute";
   // tagPage.style.top = "10px";
    tagPage.style.left = "10px";
    tagPage.style.zIndex = "" + this.tagCount;

    if ( lActiveTag )
    {
        tagPage.style.visibility = "visible";
    }
    else
    {
        tagPage.style.visibility = "hidden";
    }

    if ( !tag.visible )
    {
        tagHeader.style.visibility = "hidden";
        tagPage.style.visibility = "hidden";
    }

    document.getElementById( "tagpageContainer" ).appendChild( tagPage );
    var pageframeId = this.frameIdPrefix + this.tagCount;
    var tagPageframe = document.createElement( "iframe" );
    tagPageframe.setAttribute( "id", pageframeId );
    tagPageframe.setAttribute( "src", tag.url );
    tagPageframe.style.position = "relative";
    tagPageframe.style.top = "0px";
    tagPageframe.style.left = "0px";
    tagPageframe.frameBorder = "no";
    tagPageframe.hspace = "0";
    tagPageframe.vspace = "0";
    tagPageframe.align = "center";
    tagPageframe.width = this.ifwidth + "px";
    tagPageframe.height = this.ifheight + "px";
    document.getElementById( tagPageId ).appendChild( tagPageframe );
}

function TagContainer_createTag()
{
    var tag = new Tag();
    tag.setId( "" + ( ( this.tagCount ) * 1 + 1 ) );
    return tag;
}

function p_InitTag( rootEle, postionX, postionY, width, height )
{
    var tagheaderContainer = document.createElement( "div" );
    tagheaderContainer.setAttribute( "id", "tagheadercontainer" );
    tagheaderContainer.style.top = postionX + "px";
    tagheaderContainer.style.left = postionY + "px";
    rootEle.appendChild( tagheaderContainer );
    var tagpageContainer = document.createElement( "div" );
    tagpageContainer.setAttribute( "id", "tagpagecontainer" );
    tagpageContainer.style.width = width + "px";
    tagpageContainer.style.height = height + "px";
    tagpageContainer.style.top = ( postionX * 1 + 20 ) + "px";
    tagpageContainer.style.left = tagheaderContainer.style.left;
    rootEle.appendChild( tagpageContainer );
}

function p_InactiveTag( tag )
{
    tag.setAttribute( "class", "normaltag" );
    tag.style.display = "inline";
    tag.style.listStyleType = "none";
    tag.style.fontSize = "12px";
    tag.style.fontWeight = "normal";
    tag.style.padding = "2px 10px 2px 10px";
    tag.style.marginRight = "1px";
    tag.style.textDecoration = "none";
    tag.style.borderTopColor = "#FFFFFF";
    tag.style.borderTopStyle = "solid";
    tag.style.borderTopWidth = "1px";
    tag.style.borderRightColor = "#919B9C";
    tag.style.borderRightStyle = "solid";
    tag.style.borderRightWidth = "2px";
    tag.style.borderBottomColor = "#FFFFFF";
    tag.style.borderBottomStyle = "none";
    tag.style.borderBottomWidth = "0";
    tag.style.borderLeftColor = "#FFFFFF";
    tag.style.borderLeftStyle = "solid";
    tag.style.borderLeftWidth = "1px";
    tag.style.background = "#D4D0C8";
    tag.style.cursor = "default";
}

function p_ActiveTag( tag )
{
    tag.setAttribute( "class", "activetag" );
    tag.style.position = "relative";
    tag.style.top = "2px";
    tag.style.left = "0px";
    tag.style.display = "inline";
    tag.style.listStyleType = "none";
    tag.style.fontSize = "12px";
    tag.style.fontWeight = "normal";
    tag.style.padding = " 2px 10px 2px 10px";
    tag.style.marginRight = " 1px";
    tag.style.textDecoration = "none";
    tag.style.borderTopColor = "#FFFFFF";
    tag.style.borderTopStyle = "solid";
    tag.style.borderTopWidth = "1px";
    tag.style.borderRightColor = "#919B9C";
    tag.style.borderRightStyle = "solid";
    tag.style.borderRightWidth = "2px";
    tag.style.borderBottomColor = "#FFFFFF";
    tag.style.borderBottomStyle = "none";
    tag.style.borderBottomWidth = "0";
    tag.style.borderLeftColor = "#FFFFFF";
    tag.style.borderLeftStyle = "solid";
    tag.style.borderLeftWidth = "1px";
    tag.style.backgroundColor = "#D4D0C8";
    tag.style.cursor = "default";
}

function Tag()
{
    Tag.prototype.id = "";
    Tag.prototype.header = "";
    Tag.prototype.isActive = false;
    Tag.prototype.onTagFocus = tagFocus;
    Tag.prototype.onTagBlur = tagBlur;
    Tag.prototype.url = "";
    Tag.prototype.visible = true;
    Tag.prototype.setId = Tag_setId;
    Tag.prototype.setHeader = Tag_setHeader;
    Tag.prototype.setUrl = Tag_setUrl;
    Tag.prototype.setVisible = Tag_setVisible;
    Tag.prototype.setInvisible = Tag_setInvisible;
}

function Tag_setId( lId )
{
    this.id = lId;
}

function Tag_setHeader( lHeader )
{
    this.header = lHeader;
}

function Tag_setUrl( lUrl )
{
    this.url = lUrl;
}

function tagFocus()
{
    return true;
}

function tagBlur()
{
    return true;
}

function Tag_setVisible()
{
    this.visible = true;
}

function Tag_setInvisible()
{
    this.visible = false;
}
