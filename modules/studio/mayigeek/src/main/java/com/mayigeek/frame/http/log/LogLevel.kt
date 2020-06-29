package com.mayigeek.frame.http.log

/**
 * @author renxiao@zhiwy.com
 * @version V1.0
 * @Description: Log展示等级
 * @date 16-8-26 下午6:45
 */
enum class LogLevel {
    /**
     * No logs.
     */
    NONE,
    /**
     * Logs request and response lines.
     *
     *
     *
     * Example:
     * `--&gt; POST /greeting http/1.1 (3-byte body)
     * &lt;p/&gt;
     * &lt;-- 200 OK (22ms, 6-byte body)
    ` *
     */
    BASIC,

    /**
     * Logs request and response lines and their respective headers.
     *
     *
     *
     * Example:
     * `--&gt; POST /greeting http/1.1
     * Host: example.com
     * Content-Type: plain/text
     * Content-Length: 3
     * --&gt; END POST
     * &lt;p/&gt;
     * &lt;-- 200 OK (22ms)
     * Content-Type: plain/text
     * Content-Length: 6
     * &lt;-- END HTTP
    ` *
     */
    HEADERS,
    /**
     * Logs request and response lines and their respective headers and bodies (if present).
     *
     *
     *
     * Example:
     * `--&gt; POST /greeting http/1.1
     * Host: example.com
     * Content-Type: plain/text
     * Content-Length: 3
     * &lt;p/&gt;
     * Hi?
     * --&gt; END GET
     * &lt;p/&gt;
     * &lt;-- 200 OK (22ms)
     * Content-Type: plain/text
     * Content-Length: 6
     * &lt;p/&gt;
     * Hello!
     * &lt;-- END HTTP
    ` *
     */
    BODY
}

