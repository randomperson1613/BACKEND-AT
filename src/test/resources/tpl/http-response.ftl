<#ftl output_format="HTML">
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpResponseAttachment" -->
<div><strong>Status code:</strong> <#if data.responseCode??>${data.responseCode}<#else>Unknown</#if></div>
<#if data.url??><div>${data.url}</div></#if>

<#if data.body??>
<#assign maskedBody = data.body?replace('("token"\\s*:\\s*")[^"]+(")', '$1***$2', 'r')>
<h4>Response body</h4>
<pre class="preformated-text">${maskedBody}</pre>
</#if>

<#if (data.headers)?has_content>
<h4>Response headers</h4>
<div>
    <#list data.headers as name, value>
        <div>${name}: ${value!"null"}</div>
    </#list>
</div>
</#if>
