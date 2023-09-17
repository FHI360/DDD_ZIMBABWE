import 'package:impilo/app_state.dart';
import 'package:impilo/backend/http/api.dart';

class InventoryService {
  fulfill(String code) async {
    final response = await api.get(
        '${FFAppState().baseUrl}/api/impilo/inventory/fulfillment/site/$code');
    return response.data;
  }
}
