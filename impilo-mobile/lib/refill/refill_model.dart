import 'package:impilo/backend/floor/entities/patient.dart';

import '/flutter_flow/flutter_flow_drop_down.dart';
import '/flutter_flow/flutter_flow_icon_button.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import '/flutter_flow/flutter_flow_widgets.dart';
import '/flutter_flow/custom_functions.dart' as functions;
import 'package:smooth_page_indicator/smooth_page_indicator.dart'
    as smooth_page_indicator;
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

class RefillModel extends FlutterFlowModel {
  ///  State fields for stateful widgets in this page.

  final formKey = GlobalKey<FormState>();
  Patient? patient;
  // State field(s) for PageView widget.
  PageController? pageViewController;
  DateTime? datePicked;
  DateTime? nrd;
  // State field(s) for weight widget.
  TextEditingController? weightController;
  String? Function(BuildContext, String?)? weightControllerValidator;
  // State field(s) for systolic widget.
  TextEditingController? systolicController;
  String? Function(BuildContext, String?)? systolicControllerValidator;
  // State field(s) for diastolic widget.
  TextEditingController? diastolicController;
  String? Function(BuildContext, String?)? diastolicControllerValidator;
  // State field(s) for temperature widget.
  TextEditingController? temperatureController;
  String? Function(BuildContext, String?)? temperatureControllerValidator;
  // State field(s) for caughing widget.
  String? caughingValue;
  // State field(s) for fever widget.
  String? feverValue;
  // State field(s) for weightLoss widget.
  String? weightLossValue;
  // State field(s) for nightSweat widget.
  String? nightSweatValue;
  // State field(s) for swellings widget.
  String? swellingsValue;
  // State field(s) for tbRefer widget.
  String? tbReferValue;
  // State field(s) for qtyPrescribed widget.
  TextEditingController? qtyPrescribedController;
  String? Function(BuildContext, String?)? qtyPrescribedControllerValidator;
  // State field(s) for qtyDispensed widget.
  TextEditingController? qtyDispensedController;
  String? Function(BuildContext, String?)? qtyDispensedControllerValidator;
  // State field(s) for missedDoses widget.
  String? missedDosesValue;
  // State field(s) for adverseIssues widget.
  String? adverseIssuesValue;
  var barcode = '';

  /// Initialization and disposal methods.

  void initState(BuildContext context) {}

  void dispose() {
    weightController?.dispose();
    systolicController?.dispose();
    diastolicController?.dispose();
    temperatureController?.dispose();
    qtyPrescribedController?.dispose();
    qtyDispensedController?.dispose();
  }

  /// Additional helper methods are added here.

}
