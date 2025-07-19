package com.ronnie.toastjet.utils.apiUtils

import com.ronnie.toastjet.model.data.CookieData
import com.ronnie.toastjet.model.enums.CookieSameSite
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Extracts cookies from a map of HTTP headers.
 * It specifically looks for the "Set-Cookie" header (case-insensitive)
 * which can be a single string or a list of strings.
 *
 * @param headers A map where keys are header names (String) and values are header values (Any,
 * can be String or List<String>).
 * @return A list of `CookieData` objects parsed from the "Set-Cookie" headers.
 */
fun extractCookies(headers: Map<String, Any>): List<CookieData> {
    val cookiesList = mutableListOf<CookieData>()
    // Find the "set-cookie" header, ignoring case, and get its value.
    headers.entries.firstOrNull { it.key.equals("set-cookie", ignoreCase = true) }?.value?.let { setCookieHeader ->
        // Determine if the header value is a List of strings or a single String.
        val cookieStrings = if (setCookieHeader is List<*>) {
            setCookieHeader.filterIsInstance<String>() // Filter to ensure all elements are Strings.
        } else {
            // If it's not a list, assume it's a single string and convert it safely.
            listOf(setCookieHeader.toString())
        }

        // Iterate through each cookie string and parse it.
        cookieStrings.forEach { cookieStr ->
            parseCookie(cookieStr)?.let { cookie ->
                cookiesList.add(cookie)
            }
        }
    }
    return cookiesList
}

/**
 * Parses a single "Set-Cookie" header string into a `CookieData` object.
 * This function handles the primary key-value pair and common cookie attributes.
 *
 * @param cookieStr The raw "Set-Cookie" header string (e.g., "name=value; Expires=date; Path=/").
 * @return A `CookieData` object if parsing is successful, otherwise null.
 */
private fun parseCookie(cookieStr: String): CookieData? {
    // Split the cookie string by semicolons to separate the key-value pair from attributes.
    val parts = cookieStr.split(";")
    if (parts.isEmpty()) return null

    // The first part is always the key=value pair.
    val keyValue = parts[0].split("=", limit = 2) // Limit to 2 to handle values containing '='
    if (keyValue.size < 2) return null // A valid cookie must have a key and a value.

    val key = keyValue[0].trim()
    val value = keyValue[1].trim()

    // Initialize CookieData with the key, value, and current timestamps for creation/lastAccessed.
    val cookie = CookieData(
        key = key,
        value = value,
        creationTime = Date(),
    )

    // Define the date format for the 'Expires' attribute.
    // Use US locale for consistent parsing and GMT timezone.
    val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }

    // Iterate through the remaining parts (attributes) of the cookie string.
    for (i in 1 until parts.size) {
        val part = parts[i].trim()
        if (part.isEmpty()) continue // Skip empty parts.

        // Split each attribute part into name and optional value.
        val attributeParts = part.split("=", limit = 2)
        // Convert attribute name to lowercase for case-insensitive comparison as per RFC.
        val attrName = attributeParts[0].trim().lowercase(Locale.ROOT)
        // Get the attribute value if it exists.
        val attrValue = if (attributeParts.size > 1) attributeParts[1].trim() else null

        // Use a when expression to handle different cookie attributes.
        when (attrName) {
            "expires" -> {
                attrValue?.let {
                    try {
                        // Parse the date string and set the expires timestamp.
                        val date = parseCookieExpiresDate(it)
                        cookie.expiryTime = date
                    } catch (e: ParseException) {
                        // Log a warning if the date format is incorrect, but don't fail parsing the whole cookie.
                        System.err.println("Warning: Could not parse Expires date for cookie '$key': $it. Error: ${e.message}")
                    }
                }
            }

            "max-age" -> {
                attrValue?.let {
                    try {
                        val maxAgeSeconds = it.toLong()
                        cookie.expiryTime = Date(System.currentTimeMillis() + maxAgeSeconds * 1000)
                    } catch (e: NumberFormatException) {
                        System.err.println("Warning: Could not parse Max-Age for cookie '$key': $it. Error: ${e.message}")
                    }
                }
            }

            "domain" -> {
                if (attrValue != null) {
                    cookie.domain = attrValue
                }
            }

            "path" -> {
                if (attrValue != null) {
                    cookie.path = attrValue
                }
            }

            "secure" -> {
                cookie.secure = true
            }

            "httponly" -> {
                cookie.httpOnly = true
            }

            "samesite" -> {
                cookie.sameSite = when (attrName.uppercase()) {
                    "LAX" -> CookieSameSite.Lax
                    "STRICT" -> CookieSameSite.Strict
                    else -> CookieSameSite.None
                }
            }
        }
    }

    // Set derived properties based on whether 'Domain' and 'Path' attributes were present.
    // hostOnly is true if no 'Domain' attribute is explicitly set.
    cookie.hostOnly = cookie.domain == null
    // pathIsDefault is true if no 'Path' attribute is explicitly set (default path is usually '/').
    cookie.pathIsDefault = cookie.path == null

    return cookie
}

private fun parseCookieExpiresDate(dateString: String): Date {
    val formats = arrayOf(
        SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.US),  // RFC 1123
        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US),  // Variant
        SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.US),   // Another variant
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")             // ISO 8601
    )

    formats.forEach { format ->
        try {
            return format.parse(dateString)
        } catch (e: ParseException) {
            // Try next format
        }
    }
    throw ParseException("No matching date format found for: $dateString", 0)
}