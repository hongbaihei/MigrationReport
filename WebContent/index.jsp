<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery-ui.min.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery-ui.structure.min.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery-ui.theme.min.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/uploadify.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.min.js" ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui.min.js" ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.uploadify.min.js" ></script>
 <script type="text/javascript">
        $(function () {
            $("#uploadify").uploadify({
                //指定swf文件
                'swf': 'uploadify/uploadify.swf',
                //后台处理的页面
                'uploader': '${pageContext.request.contextPath}/upload',
                //按钮显示的文字
                'buttonText': '上传报表',
                //显示的高度和宽度，默认 height 30；width 120
                //'height': 15,
                //'width': 80,
                //上传文件的类型  默认为所有文件    'All Files'  ;  '*.*'
                //在浏览窗口底部的文件类型下拉菜单中显示的文本
                'fileTypeDesc': 'Excel Files',
                //允许上传的文件后缀
                'fileTypeExts': '*.xls; *.xlsx; ',
                //发送给后台的其他参数通过formData指定
                //'formData': { 'someKey': 'someValue', 'someOtherKey': 1 },
                //上传文件页面中，你想要用来作为文件队列的元素的id, 默认为false  自动生成,  不带#
                //'queueID': 'fileQueue',
                //选择文件后自动上传
                'auto': true,
                //设置为true将允许多文件上传
                'multi': true
            });
        });
    
    </script>

<body>
	<div align="center">
        <%--用来作为文件队列区域--%>
        <div id="fileQueue" >
        </div>
        <input type="file" name="uploadify" id="uploadify" />
        <p>
            <a href="javascript:$('#uploadify').uploadify('upload')">上传</a>| 
            <a href="javascript:$('#uploadify').uploadify('cancel')">取消上传</a>
        </p>
    </div>
</body>