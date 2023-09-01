import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:csv/csv.dart';
import 'flutter_flow/lat_lng.dart';

class FFAppState extends ChangeNotifier {
  static final FFAppState _instance = FFAppState._internal();

  factory FFAppState() {
    return _instance;
  }

  FFAppState._internal() {
    initializePersistedState();
  }

  Future initializePersistedState() async {
    secureStorage = FlutterSecureStorage();
    _code = await secureStorage.getString('ff_code') ?? _code;
    _baseUrl = await secureStorage.getString('ff_baseUrl') ?? _baseUrl;
    _name = await secureStorage.getString('ff_name') ?? _name;
    _accessToken = await secureStorage.getString('ff_accessToken') ?? _accessToken;
    _refreshToken = await secureStorage.getString('ff_refreshToken') ?? _refreshToken;
    _regimens = await secureStorage.getStringList('ff_regimens') ?? _regimens;
  }

  void update(VoidCallback callback) {
    callback();
    notifyListeners();
  }

  late FlutterSecureStorage secureStorage;

  String _code = '';
  String get code => _code;
  set code(String _value) {
    _code = _value;
    secureStorage.setString('ff_code', _value);
  }

  void deleteCode() {
    secureStorage.delete(key: 'ff_code');
  }

  String _baseUrl = '';
  String get baseUrl => _baseUrl;
  set baseUrl(String _value) {
    _baseUrl = _value;
    secureStorage.setString('ff_baseUrl', _value);
  }

  String _accessToken = '';

  String get accessToken => _accessToken;

  set accessToken(String _value) {
    _accessToken = _value;
    secureStorage.setString('ff_accessToken', _value);
  }

  String _refreshToken = '';

  String get refreshToken => _refreshToken;

  set refreshToken(String _value) {
    _refreshToken = _value;
    secureStorage.setString('ff_refreshToken', _value);
  }

  String _name = '';
  String get name => _name;
  set name(String _value) {
    _name = _value;
    secureStorage.setString('ff_name', _value);
  }

  void deleteName() {
    secureStorage.delete(key: 'ff_name');
  }

  List<String> _regimens = [];
  List<String> get regimens => _regimens;
  set regimens(List<String> _value) {
    _regimens = _value;
  }

  void addToRegimens(String _value) {
    _regimens.add(_value);
  }

  void removeFromRegimens(String _value) {
    _regimens.remove(_value);
  }

  void removeAtIndexFromRegimens(int _index) {
    _regimens.removeAt(_index);
  }

  int _regimenQty = 0;
  int get regimenQty => _regimenQty;
  set regimenQty(int _value) {
    _regimenQty = _value;
  }
}

extension FlutterSecureStorageExtensions on FlutterSecureStorage {
  void remove(String key) => delete(key: key);

  Future<String?> getString(String key) async => await read(key: key);
  Future<void> setString(String key, String value) async =>
      await write(key: key, value: value);

  Future<bool?> getBool(String key) async => (await read(key: key)) == 'true';
  Future<void> setBool(String key, bool value) async =>
      await write(key: key, value: value.toString());

  Future<int?> getInt(String key) async =>
      int.tryParse(await read(key: key) ?? '');
  Future<void> setInt(String key, int value) async =>
      await write(key: key, value: value.toString());

  Future<double?> getDouble(String key) async =>
      double.tryParse(await read(key: key) ?? '');
  Future<void> setDouble(String key, double value) async =>
      await write(key: key, value: value.toString());

  Future<List<String>?> getStringList(String key) async =>
      await read(key: key).then((result) {
        if (result == null || result.isEmpty) {
          return null;
        }
        return CsvToListConverter()
            .convert(result)
            .first
            .map((e) => e.toString())
            .toList();
      });
  Future<void> setStringList(String key, List<String> value) async =>
      await write(key: key, value: ListToCsvConverter().convert([value]));
}
