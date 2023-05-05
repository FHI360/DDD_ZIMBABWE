import 'package:flutter/services.dart';
import 'package:impilo/backend/floor/dao/refill_dao.dart';

import '/flutter_flow/flutter_flow_util.dart';
import '../main.dart';

Future<String> estimatedRefillPdf(DateTime start, DateTime end) async {
  var _database = await database;
  List<EstimatedRefill> rows =
      await _database.refillDao.estimatedRefill(FFAppState().code, start, end);

  String style = await rootBundle.loadString('assets/css/bootstrap.min.css');
  String content = '''
  <div class="row">
    <div class="col-12 justify-content-center m-3">
      <h1 class="text-center">${FFAppState().name}</h1>
    </div>
  </div>
  <div class="row">
    <div class="col-12 justify-content-center mb-3">
      <h2 class="text-center">Estimated Stock requirement from ${dateTimeFormat('yMMMd', start)} to ${dateTimeFormat('yMMMd', end)}</h2>
    </div>
  </div>
  <div class="row">
    <div class="col-12">
      <table class="table table-sm table-striped">
        <thead class="table-light">
        <tr>
            <th scope="col" colspan="1" class="text-center">#</th>
            <th scope="col" colspan="4" class="text-center">Regimen</th>
            <th scope="col" colspan="2" class="text-center">Quantity Required</th>
        </tr>
        </thead>   
        <tbody>
  ''';
  rows.mapIndexed((i, e) {
    content += '''
      <tr>
        <th scope="row">${i + 1}</th>
        <td class="justify-content-start" colspan="4">${e.regimen}</td>
        <td class="justify-content-start" colspan="3">${formatNumber(
      e.qty,
      formatType: FormatType.custom,
      format: '#,##0',
      locale: '',
    )}</td>
      </tr>
    ''';
  });

  content += '</tbody></table></div></div>';

  String html = '''
  <!DOCTYPE html>
  <html lang="en">
    <style>
      $style
    </style>
    <body>
      <div class="container">
        $content
      </div>
    </body>
  </html>
  ''';
  return html;
}
