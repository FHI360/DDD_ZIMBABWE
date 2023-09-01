const {
    setInferVersion,
    withModuleFederationPlugin,
    share
} = require('@angular-architects/module-federation/webpack');
setInferVersion(true);

module.exports = withModuleFederationPlugin({
    name: "_5e665722_af83_4c02_82c0_48e62d779674",
    //This section relates directly to the webRemote section of plugin.yml
    //The key is the exposedName for components or name for modules and the value is the path to the
    //respective Angular component or module in your web project. For other web frameworks, it is
    //usually the path to the web element exposing your component
    exposes: {
        'StockModule': './src/main/webapp/app/stock/stock.module.ts',
        'StockIssuanceModule': './src/main/webapp/app/stock-issuance/stock-issuance.module.ts',
        'DevolveModule': './src/main/webapp/app/devolve/devolve.module.ts',
        'StockRequestModule': './src/main/webapp/app/stock-request/stock.request.module.ts',
        'IndicatorRouting': './src/main/webapp/app/reporting/indicator/indicator.routing.ts',
        'SyncRouting': './src/main/webapp/app/synchronisation/synchronization.routing.ts',
    },
    shared: share({
        "@angular/core": {singleton: true, strictVersion: false},
        "@angular/common": {singleton: true, strictVersion: false},
        "@angular/common/http": {singleton: true, strictVersion: false},
        "@angular/router": {singleton: true, strictVersion: false},
        "@angular/forms": {singleton: true, strictVersion: false},
        "@angular/animations": {singleton: true, strictVersion: false},
        "@mattae/angular-shared": {singleton: true, strictVersion: false},
        "@ngrx/store": {singleton: true, strictVersion: false},
        "@angular/material": {singleton: true, includeSecondaries: true, strictVersion: false},
        "@ngneat/transloco": {singleton: true, strictVersion: false}
    })
});

