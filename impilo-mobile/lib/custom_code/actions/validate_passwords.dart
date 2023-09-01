// Automatic FlutterFlow imports

// Begin custom action code
// DO NOT REMOVE OR MODIFY THE CODE ABOVE!

// DO NOT REMOVE OR MODIFY THE CODE ABOVE!return Future.value(true);

Future<bool> validatePasswords(
  String password,
  String confirm,
) async {
  // Add your function code here!
  if (password == confirm) {
    return Future.value(true);
  }
  return Future.value(false);
}
