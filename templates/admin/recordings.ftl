<#import "layout/defaultLayout.ftl" as layout>
<@layout.myLayout "Recordings">
  <div><h2>Recordings</h2></div>
  <#assign i = 0>
  <#list recordings as recording>
    <div>

    <table border="1">
    <tr>
    <th><h3>${recording.title}</h3></th>
    <#if recording.subtitle??>
      <th><h4>${recording.subtitle}</h4></th>
    <#else>
      <th><h4>No subtitle</h4></th>
    </#if>
    </tr>
    <tr>
    <#if recording.description??>
      <td><p>${recording.description}</p></td>
    <#else>
      <td><h4>No description</h4></td>
    </#if>
    <#if screenshots[i]??>
      <td><img src="${screenshots[i]}" alt="Missing" width="534" height="300"></td>
    <#else>
      <td><h4>no screenshot</h4></td>
    </#if>
    </tr>
    </table>
    </div>
    <#assign i = i + 1>
  </#list>
</@layout.myLayout>
