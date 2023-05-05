import '../backend/api_requests/api_manager.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import '/flutter_flow/flutter_flow_widgets.dart';
import '/custom_code/actions/index.dart' as actions;
import 'package:flutter/material.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

class ActivateSiteModel extends FlutterFlowModel {
  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  // State field(s) for code widget.
  TextEditingController? codeController;
  String? Function(BuildContext, String?)? codeControllerValidator;
  // Stores action output result for [Custom Action - activateSite] action in Button-Login widget.
  bool? siteCode;

  ApiCallResponse? patients;

  /// Initialization and disposal methods.

  void initState(BuildContext context) {}

  void dispose() {
    codeController?.dispose();
  }

  /// Additional helper methods are added here.

}
