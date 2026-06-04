<#ftl output_format="HTML">
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpRequestAttachment" -->
<div><strong><#if data.method??>${data.method}<#else>GET</#if></strong> <#if data.url??>${data.url}<#else>Unknown URL</#if></div>

<#if data.body??>
<#assign maskedBody = data.body?replace('("password"\\s*:\\s*")[^"]+(")', '$1***$2', 'r')>
<h4>Request body</h4>
<pre class="preformated-text">${maskedBody}</pre>
</#if>

<#if (data.headers)?has_content>
<h4>Request headers</h4>
<div>
    <#list data.headers as name, value>
        <div>${name}: <#if name?lower_case == "x-auth-token" || name?lower_case == "authorization">***<#else>${value!"null"}</#if></div>
    </#list>
</div>
</#if>

<#if (data.formParams)?has_content>
<h4>Form params</h4>
<div>
    <#list data.formParams as name, value>
        <div>${name}: ${value!"null"}</div>
    </#list>
</div>
</#if>
