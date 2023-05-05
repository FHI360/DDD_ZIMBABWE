import 'package:firebase_auth/firebase_auth.dart';
import 'package:rxdart/rxdart.dart';

class ImpiloFirebaseUser {
  ImpiloFirebaseUser(this.user);
  User? user;
  bool get loggedIn => user != null;
}

ImpiloFirebaseUser? currentUser;
bool get loggedIn => currentUser?.loggedIn ?? false;
Stream<ImpiloFirebaseUser> impiloFirebaseUserStream() => FirebaseAuth.instance
        .authStateChanges()
        .debounce((user) => user == null && !loggedIn
            ? TimerStream(true, const Duration(seconds: 1))
            : Stream.value(user))
        .map<ImpiloFirebaseUser>(
      (user) {
        currentUser = ImpiloFirebaseUser(user);
        return currentUser!;
      },
    );
