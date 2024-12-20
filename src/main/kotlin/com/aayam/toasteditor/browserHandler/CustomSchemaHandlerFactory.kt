package com.aayam.toasteditor.browserHandler

import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.callback.CefSchemeHandlerFactory
import org.cef.network.CefRequest

class CustomSchemeHandlerFactory : CefSchemeHandlerFactory {

    override fun create(
        browser: CefBrowser?,
        frame: CefFrame?,
        schemeName: String?,
        request: CefRequest?
    ): CustomResourceHandler {
        return CustomResourceHandler()
    }
}
