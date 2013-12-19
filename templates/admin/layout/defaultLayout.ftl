<#macro myLayout title="FreeMarker example">
  <html>
    <head>
      <#include "header.ftl"/>
      <title>
        ${title}
      </title>
    </head>
    <body style="width:100%;height:100%">
<!-- Nav Bar -->
 
  <div class="row">
    <#include "menu.ftl"/>
  </div>
 
  <!-- End Nav -->
 
 
  <!-- Main Page Content and Sidebar -->
 
  <div class="row">
 
    <!-- Main Blog Content -->
    <div class="large-9 columns" role="content">
 
        <#nested/>

    </div>
 
    <!-- End Main Content -->
 
 
    <!-- Sidebar -->
 
    <aside class="large-3 columns">
 
      <div class="panel">
        <h5>About</h5>
        <p>The jflicks media system allows you to watch your own recorded and library media.</p>
        <a href="#">Read More →</a>
      </div>
 
      <#include "sidebar-cats.ftl"/>

    </aside>
 
    <!-- End Sidebar -->
  </div>
 
  <!-- End Main Content and Sidebar -->
 
 
  <!-- Footer -->
 
  <footer class="row">
    <#include "footer.ftl"/>
  </footer>
    
    </body>
  </html>
</#macro>
