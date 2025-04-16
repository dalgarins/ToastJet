package com.ronnie.toastjet.model.data

data class ConfigData(
    var description: String="",
    var swaggerConfig: String="",
    var functions: String="",

    var cookie: MutableList<CookieData> = mutableListOf(),
    var vars: MutableList<KeyValueChecked> = mutableListOf(),
    var envs: MutableList<EnvData> = mutableListOf(),
    var envPath: MutableList<String> = mutableListOf(),

    var title: String = "",
    var baseDomain: String = "",

    var enableProxy: Boolean= false,
    var proxyAddress: String = "",
    var proxyUsername: String = "",
    var proxyPassword: String = "",

    var enableSsh: Boolean = false,
    var sshAddress: String = "",
    var sshPort: Int = 80,
    var sshUsername: String = "",
    var sshPassword: String = "",
)
