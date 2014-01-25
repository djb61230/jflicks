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
    <td style="width:49%;">
    <#if recording.description??>
      <p>${recording.description}</p>
    <#else>
      <h4>No description</h4>
    </#if>
    </td>
    <td style="width:49%;">
    <#if screenshots[i]??>
      <img src="${screenshots[i]}" alt="Missing" width="534" height="300">
    <#else>
      <h4>no screenshot</h4>
    </#if>
    </td>
    </tr>
    </table>
    </div>
    <#assign i = i + 1>
  </#list>
</@layout.myLayout>
