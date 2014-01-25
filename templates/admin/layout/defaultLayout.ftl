<#macro myLayout title="FreeMarker example">
  <html>
    <head>
      <#include "header.ftl"/>
      <title>
        ${title}
      </title>
    </head>
    <body >
<!-- Nav Bar -->
  <div class="row">
    <#include "menu.ftl"/>
  </div>
 
  <!-- End Nav -->
 
 
  <!-- Main Page Content and Sidebar -->
 
   <div id="sidebar"> 
 
      <div class="panel">
        <h5>About</h5>
        <p>The jflicks media system allows you to watch your own recorded and library media.</p>
        <a href="#">Read More →</a>
      </div>
 
      <#include "sidebar-cats.ftl"/>

    </div>
  <div id="content">
 
    <!-- Main Blog Content -->
 
        <#nested/>

 
    <!-- End Main Content -->
 
</div> 
    <!-- Sidebar -->
 
 
    <!-- End Sidebar -->
  
 
  <!-- End Main Content and Sidebar -->
 
 
  <!-- Footer -->
 
  <footer class="row">
    <#include "footer.ftl"/>
  </footer>
    
    </body>
  </html>
</#macro>
