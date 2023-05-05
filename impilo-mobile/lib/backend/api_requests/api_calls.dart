import 'dart:convert';

import 'package:logging/logging.dart';

import '../../flutter_flow/flutter_flow_util.dart';
import 'api_manager.dart';

export 'api_manager.dart' show ApiCallResponse;

const BASE_URL = 'http://196.43.115.18';

class SiteActivationCall {
  static Future<ApiCallResponse> call({
    String? code = '',
  }) {
    return ApiManager.instance.makeApiCall(
      callName: 'Site Activation',
      apiUrl: '$BASE_URL/api/impilo/activation/activate/site/$code',
      callType: ApiCallType.GET,
      headers: {},
      params: {},
      returnBody: true,
      encodeBodyUtf8: false,
      decodeUtf8: false,
      cache: false,
    );
  }

  static dynamic patients(dynamic response) => getJsonField(
        response,
        r'''$''',
        true,
      );

  static dynamic site(dynamic response) => getJsonField(
        response,
        r'''$.site''',
      );
}

class InventoryFulfillmentCall {
  static Future<ApiCallResponse> call({
    String code = '',
    String uniqueId = '',
  }) {
    code = code.toUpperCase();
    return ApiManager.instance.makeApiCall(
      callName: 'Inventory Fulfillment',
      apiUrl:
          '$BASE_URL/api/impilo/inventory/fulfillment/site/$code/unique-id/$uniqueId',
      callType: ApiCallType.GET,
      headers: {},
      params: {},
      returnBody: true,
      encodeBodyUtf8: false,
      decodeUtf8: false,
      cache: false,
    );
  }
}

class DiscontinueServiceCall {
  static Future<ApiCallResponse> call({
    required String siteCode,
    required String uniqueId,
    required String date,
    required String reason,
  }) {
    final body = '''
{
  "date": "$date",
  "reason": "$reason"
}''';
    return ApiManager.instance.makeApiCall(
      callName: 'Service Discontinuation Request',
      apiUrl:
          '$BASE_URL/api/impilo/activation/discontinue-services/client/$uniqueId/site/$siteCode',
      callType: ApiCallType.POST,
      headers: {},
      params: {},
      body: body,
      bodyType: BodyType.JSON,
      returnBody: true,
      encodeBodyUtf8: false,
      decodeUtf8: false,
      cache: false,
    );
  }
}

class InventoryRequestCall {
  static Future<ApiCallResponse> call({
    String? siteCode = '',
    String? uniqueId = '',
    String? date = '',
    dynamic? itemsJson,
  }) {
    final items = _serializeJson(itemsJson);
    final body = items;
    return ApiManager.instance.makeApiCall(
      callName: 'Inventory Request',
      apiUrl:
          '$BASE_URL/api/impilo/inventory/request/site/$siteCode/date/$date/unique-id/$uniqueId',
      callType: ApiCallType.POST,
      headers: {},
      params: {},
      body: body,
      bodyType: BodyType.JSON,
      returnBody: true,
      encodeBodyUtf8: false,
      decodeUtf8: false,
      cache: false,
    );
  }
}

class AcknowledgeInventoryCall {
  static Future<ApiCallResponse> call({
    String? siteCode = '',
    String? uniqueId = ''
  }) {
    return ApiManager.instance.makeApiCall(
      callName: 'Inventory Acknowledge',
      apiUrl:
      '$BASE_URL/api/impilo/inventory/acknowledge/site/$siteCode/unique-id/$uniqueId',
      callType: ApiCallType.GET,
      headers: {},
      params: {},
      bodyType: BodyType.JSON,
      returnBody: false,

      encodeBodyUtf8: false,
      decodeUtf8: false,
      cache: false,
    );
  }
}

class ApiPagingParams {
  int nextPageNumber = 0;
  int numItems = 0;
  dynamic lastResponse;

  ApiPagingParams({
    required this.nextPageNumber,
    required this.numItems,
    required this.lastResponse,
  });

  @override
  String toString() =>
      'PagingParams(nextPageNumber: $nextPageNumber, numItems: $numItems, lastResponse: $lastResponse,)';
}

String _serializeList(List? list) {
  list ??= <String>[];
  try {
    return json.encode(list);
  } catch (_) {
    return '[]';
  }
}

String _serializeJson(dynamic jsonVar) {
  jsonVar ??= {};
  try {
    return json.encode(jsonVar);
  } catch (_) {
    return '{}';
  }
}
