import 'package:impilo/backend/http/api.dart';

class SiteActivationApi {
  Future<dynamic> activate(String siteId) async {
    try {
      final response = await api.get('api/impilo/activation/activate/$siteId');
      return response.data;
    } catch (e) {}
    return Future.value({});
  }
}
