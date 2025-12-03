const path = require('path');

module.exports = {
  publicPath: './',
  outputDir: path.resolve(__dirname, '../resources/static'),
  devServer: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
};