const path = require('path');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const ManifestPlugin = require('webpack-manifest-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const TerserJSPlugin = require('terser-webpack-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');

module.exports = env => {
  let isDev = env.development;
  let isProd = env.production;

  let minimizers = isProd ? [new TerserJSPlugin({}), new OptimizeCSSAssetsPlugin({})] : [];

  return {
    mode: 'development',
    entry: {
      main: './src/index.js',
      write: './src/write/write.js'
    },
    output: {
      filename: '[name].[contenthash].js',
      path: path.resolve(__dirname, '../src/main/webapp/static/dist')
    },
    optimization: {
      minimizer: minimizers,
      moduleIds: 'hashed',
      runtimeChunk: 'single',
      splitChunks: {
        cacheGroups: {
          vendor: {
            test: /[\\/]node_modules[\\/]/,
            name: 'vendors',
            chunks: 'all',
          },
        }
      }
    },
    module: {
      rules: [
        {
          test: /\.js$/,
          include: path.resolve(__dirname, 'src'),
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-env']
          }
        },
        {
          test: /\.css$/,
          include: path.resolve(__dirname, 'src'),
          use: [
            MiniCssExtractPlugin.loader,
            'css-loader'
          ]
        },
        {
          test: /\.(png|svg|jpg|gif)$/,
          include: path.resolve(__dirname, 'src'),
          use: [
            'file-loader'
          ]
        },
        {
          test: /\.(woff|woff2|eot|ttf|otf)$/,
          include: path.resolve(__dirname, 'src'),
          use: [
            'file-loader'
          ]
        }
      ]
    },
    devtool: isDev ? 'inline-source-map' : 'source-map',
    plugins: [
      new CleanWebpackPlugin(),
      new ManifestPlugin(),
      new MiniCssExtractPlugin({
        filename: '[name].[contenthash].css',
        chunkFilename: '[id].[contenthash].css'
      })
    ]
  };
};

function optFilter(...objectsWithOptions) {
  return objectsWithOptions.filter(c => c[1]).map(c => c[0]);
}