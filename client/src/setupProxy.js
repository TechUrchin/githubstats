const { createProxyMiddleware } = require('http-proxy-middleware');
module.exports = function (app) {
    app.use(createProxyMiddleware('/api/stats',
        {target: 'http://localhost:8000/', "logLevel": 'debug'}));
    app.use(createProxyMiddleware('/api/reports',
        {target:'http://localhost:8080/', logLevel: 'debug'}));
    // app.use(createProxyMiddleware('/api/github',
    //     {target:'https://api.github.com/', logLevel: 'debug'}));
}