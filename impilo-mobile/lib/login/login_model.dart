import 'package:flutter/material.dart';

import '/flutter_flow/flutter_flow_util.dart';

class LoginModel extends FlutterFlowModel {
  ///  State fields for stateful widgets in this page.
  final formKey = GlobalKey<FormState>();

  // State field(s) for emailAddress widget.
  TextEditingController? emailAddressController;
  String? Function(BuildContext, String?)? emailAddressControllerValidator;
  // State field(s) for password widget.
  TextEditingController? passwordController;
  late bool passwordVisibility;
  String? Function(BuildContext, String?)? passwordControllerValidator;
  TextEditingController? baseUrlController;
  String? Function(BuildContext, String?)? baseUrlControllerValidator;

  String? _baseUrlControllerValidator(BuildContext context, String? val) {
    if (val == null || val.isEmpty) {
      return 'Field is required';
    }

    final regExp = RegExp(
        r'^(https?://)?([\da-z.-]+)(\.[a-z.]{2,6})?(:\d{1,5})?$',
        caseSensitive: false,
        multiLine: false);

    if (!regExp.hasMatch(val)) {
      return 'URL is not valid';
    }

    return null;
  }

  /// Initialization and disposal methods.

  void initState(BuildContext context) {
    passwordVisibility = false;
    baseUrlControllerValidator = _baseUrlControllerValidator;
  }

  void dispose() {
    emailAddressController?.dispose();
    passwordController?.dispose();
    baseUrlController?.dispose();
  }

  /// Additional helper methods are added here.

}
