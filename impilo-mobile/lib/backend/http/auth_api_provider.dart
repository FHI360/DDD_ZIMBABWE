import '../../app_state.dart';
import 'api.dart';

class AuthAPIProvider {
  Future<dynamic> authenticate(
      {required String username, required String password}) async {
    final response = await api.post(
      '/api/authenticate',
      data: {
        'username': username,
        'password': password,
      },
    );
    return response.data;
  }

  Future<void> processProfile() async {
    final response = await api.get(
      '/api/account',
      options: Options(
        headers: {},
      ),
    );

    print('Response: $response');

    FFAppState().code = response.data['organisation']['id'];
    FFAppState().name = response.data['organisation']['name'];
  }
}
