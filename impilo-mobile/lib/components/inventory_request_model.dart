import 'package:flutter/material.dart';

import '/flutter_flow/flutter_flow_util.dart';

class InventoryRequestModel extends FlutterFlowModel {
  ///  Local state fields for this component.

  String? error;

  ///  State fields for stateful widgets in this component.

  final formKey = GlobalKey<FormState>();

  // State field(s) for regimen widget.
  String? regimenValue;

  // State field(s) for TextField widget.
  TextEditingController? textController;
  String? Function(BuildContext, String?)? textControllerValidator;


  String? _numericControllerValidator(BuildContext context, String? val) {
    if (val != null && int.tryParse(val) == null) {
      return 'Please input a number.';
    }

    if (val != null && int.tryParse(val) != null && int.parse(val) < 1) {
      return 'Minimum value is 1.';
    }

    return null;
  }

  /// Initialization and disposal methods.

  void initState(BuildContext context) {
    textControllerValidator = _numericControllerValidator;
  }

  void dispose() {
    textController?.dispose();
  }

  /// Additional helper methods are added here.
}
