import 'package:easy_debounce/easy_debounce.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_barcode_scanner/flutter_barcode_scanner.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:impilo/backend/floor/entities/clinic_data.dart';
import 'package:impilo/backend/floor/entities/refill.dart';
import 'package:impilo/main.dart';
import 'package:provider/provider.dart';
import 'package:smooth_page_indicator/smooth_page_indicator.dart'
    as smooth_page_indicator;

import '/custom_code/actions/index.dart' as actions;
import '/flutter_flow/custom_functions.dart' as functions;
import '/flutter_flow/flutter_flow_drop_down.dart';
import '/flutter_flow/flutter_flow_icon_button.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import '/flutter_flow/flutter_flow_widgets.dart';
import '../custom_code/actions/index.dart';
import 'refill_model.dart';

export 'refill_model.dart';

class RefillWidget extends StatefulWidget {
  const RefillWidget({
    Key? key,
    this.patientId,
  }) : super(key: key);

  final int? patientId;

  @override
  _RefillWidgetState createState() => _RefillWidgetState();
}

class _RefillWidgetState extends State<RefillWidget> {
  late RefillModel _model;

  final scaffoldKey = GlobalKey<ScaffoldState>();
  final _unfocusNode = FocusNode();

  @override
  void initState() {
    super.initState();
    _model = createModel(context, () => RefillModel());

    _model.weightController ??= TextEditingController();
    _model.systolicController ??= TextEditingController();
    _model.diastolicController ??= TextEditingController();
    _model.temperatureController ??= TextEditingController();
    _model.qtyPrescribedController ??= TextEditingController();
    _model.qtyDispensedController ??= TextEditingController();

    WidgetsBinding.instance.addPostFrameCallback((_) async {
      var _database = await database;
      var patient = await _database.patientDao.findById(widget.patientId!);
      setState(() {
        _model.patient = patient;
      });
      setState(() {});
    });
  }

  @override
  void dispose() {
    _model.dispose();

    _unfocusNode.dispose();
    super.dispose();
  }

  bool nextVisitIsBeforeNextVLAppointment() {
    return _model.patient != null &&
        int.tryParse(_model.qtyDispensedController.text) != null &&
        _model.datePicked != null &&
        _model.datePicked!
            .add(Duration(days: int.parse(_model.qtyDispensedController.text)))
            .isBefore(_model.patient!.nextViralLoadDate) &&
        _model.patient!.nextViralLoadDate.isAfter(DateTime(1900));
  }

  bool nextVisitIsAfterNextVLAppointment() {
    return _model.patient != null &&
        int.tryParse(_model.qtyDispensedController.text) != null &&
        _model.datePicked != null &&
        _model.datePicked!
            .add(Duration(days: int.parse(_model.qtyDispensedController.text)))
            .isAfter(_model.patient!.nextViralLoadDate) &&
        _model.patient!.nextViralLoadDate.isAfter(DateTime(1900));
  }

  bool nextVisitIsBeforeNextTPTAppointment() {
    return _model.patient != null &&
        int.tryParse(_model.qtyDispensedController.text) != null &&
        _model.datePicked != null &&
        _model.datePicked!
            .add(Duration(days: int.parse(_model.qtyDispensedController.text)))
            .isBefore(_model.patient!.nextTptDate) &&
        _model.patient!.nextTptDate.isAfter(DateTime(1900));
  }

  bool nextVisitIsAfterNextTPTAppointment() {
    return _model.patient != null &&
        int.tryParse(_model.qtyDispensedController.text) != null &&
        _model.datePicked != null &&
        _model.datePicked!
            .add(Duration(days: int.parse(_model.qtyDispensedController.text)))
            .isAfter(_model.patient!.nextTptDate) &&
        _model.patient!.nextTptDate.isAfter(DateTime(1900));
  }

  bool nextVisitIsBeforeNextCancerScreeningAppointment() {
    return _model.patient != null &&
        _model.patient!.sex == 'Female' &&
        int.tryParse(_model.qtyDispensedController.text) != null &&
        _model.datePicked != null &&
        _model.datePicked!
            .add(Duration(days: int.parse(_model.qtyDispensedController.text)))
            .isBefore(_model.patient!.nextCervicalCancerDate) &&
        _model.patient!.nextCervicalCancerDate.isAfter(DateTime(1900));
  }

  bool nextVisitIsAfterNextCancerScreeningAppointment() {
    return _model.patient != null &&
        _model.patient!.sex == 'Female' &&
        int.tryParse(_model.qtyDispensedController.text) != null &&
        _model.datePicked != null &&
        _model.datePicked!
            .add(Duration(days: int.parse(_model.qtyDispensedController.text)))
            .isAfter(_model.patient!.nextCervicalCancerDate) &&
        _model.patient!.nextCervicalCancerDate.isAfter(DateTime(1900));
  }

  @override
  Widget build(BuildContext context) {
    context.watch<FFAppState>();

    return Scaffold(
      key: scaffoldKey,
      backgroundColor: FlutterFlowTheme.of(context).primaryBackground,
      appBar: AppBar(
        backgroundColor: FlutterFlowTheme.of(context).primaryColor,
        automaticallyImplyLeading: false,
        leading: FlutterFlowIconButton(
          borderColor: Colors.transparent,
          borderRadius: 30,
          borderWidth: 1,
          buttonSize: 60,
          icon: Icon(
            Icons.arrow_back_rounded,
            color: Colors.white,
            size: 30,
          ),
          onPressed: () async {
            context.pop();
          },
        ),
        title: Text(
          'Patient Refill',
          style: FlutterFlowTheme.of(context).title2.override(
                fontFamily: FlutterFlowTheme.of(context).title2Family,
                color: Colors.white,
                fontSize: 22,
                useGoogleFonts: GoogleFonts.asMap()
                    .containsKey(FlutterFlowTheme.of(context).title2Family),
              ),
        ),
        actions: [],
        centerTitle: true,
        elevation: 2,
      ),
      body: SafeArea(
        child: GestureDetector(
          onTap: () => FocusScope.of(context).requestFocus(_unfocusNode),
          child: Container(
            width: double.infinity,
            height: double.infinity,
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [
                  FlutterFlowTheme.of(context).primaryColor,
                  Color(0xFFF5F5F5)
                ],
                stops: [0, 1],
                begin: AlignmentDirectional(0, -1),
                end: AlignmentDirectional(0, 1),
              ),
            ),
            child: Column(
              mainAxisSize: MainAxisSize.max,
              children: [
                Expanded(
                  child: Form(
                    key: _model.formKey,
                    autovalidateMode: AutovalidateMode.always,
                    child: Padding(
                      padding: EdgeInsetsDirectional.fromSTEB(10, 10, 10, 10),
                      child: Container(
                        width: double.infinity,
                        height: 500,
                        child: Stack(
                          children: [
                            Padding(
                              padding:
                                  EdgeInsetsDirectional.fromSTEB(0, 0, 0, 50),
                              child: PageView(
                                controller: _model.pageViewController ??=
                                    PageController(initialPage: 0),
                                scrollDirection: Axis.horizontal,
                                children: [
                                  Padding(
                                    padding: EdgeInsetsDirectional.fromSTEB(
                                        0, 40, 0, 0),
                                    child: Card(
                                      clipBehavior: Clip.antiAliasWithSaveLayer,
                                      color:
                                          FlutterFlowTheme.of(context).overlay,
                                      shape: RoundedRectangleBorder(
                                        borderRadius: BorderRadius.circular(5),
                                      ),
                                      child: Padding(
                                        padding: EdgeInsetsDirectional.fromSTEB(
                                            10, 10, 10, 10),
                                        child: Column(
                                          mainAxisSize: MainAxisSize.max,
                                          children: [
                                            Padding(
                                              padding: EdgeInsetsDirectional
                                                  .fromSTEB(0, 10, 0, 0),
                                              child: Row(
                                                mainAxisSize: MainAxisSize.max,
                                                mainAxisAlignment:
                                                    MainAxisAlignment.center,
                                                children: [
                                                  Text(
                                                    'Vitals',
                                                    style: FlutterFlowTheme.of(
                                                            context)
                                                        .title2,
                                                  ),
                                                ],
                                              ),
                                            ),
                                            Padding(
                                              padding: EdgeInsetsDirectional
                                                  .fromSTEB(0, 16, 0, 0),
                                              child: Material(
                                                color: Colors.transparent,
                                                elevation: 0,
                                                shape: RoundedRectangleBorder(
                                                  borderRadius:
                                                      BorderRadius.circular(8),
                                                ),
                                                child: Container(
                                                  width: MediaQuery.of(context)
                                                          .size
                                                          .width *
                                                      0.9,
                                                  height: 60,
                                                  decoration: BoxDecoration(
                                                    boxShadow: [
                                                      BoxShadow(
                                                        blurRadius: 2,
                                                        color:
                                                            Color(0x33000000),
                                                        offset: Offset(0, 2),
                                                      )
                                                    ],
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                    border: Border.all(
                                                      width: 1,
                                                    ),
                                                  ),
                                                  child: Row(
                                                    mainAxisSize:
                                                        MainAxisSize.max,
                                                    children: [
                                                      Expanded(
                                                        child: Column(
                                                          mainAxisSize:
                                                              MainAxisSize.max,
                                                          mainAxisAlignment:
                                                              MainAxisAlignment
                                                                  .center,
                                                          crossAxisAlignment:
                                                              CrossAxisAlignment
                                                                  .start,
                                                          children: [
                                                            Padding(
                                                              padding:
                                                                  EdgeInsetsDirectional
                                                                      .fromSTEB(
                                                                          20,
                                                                          0,
                                                                          0,
                                                                          0),
                                                              child: Text(
                                                                'Visit Date',
                                                                style: FlutterFlowTheme.of(
                                                                        context)
                                                                    .bodyText1
                                                                    .override(
                                                                      fontFamily:
                                                                          FlutterFlowTheme.of(context)
                                                                              .bodyText1Family,
                                                                      fontSize:
                                                                          12,
                                                                      useGoogleFonts: GoogleFonts
                                                                              .asMap()
                                                                          .containsKey(
                                                                              FlutterFlowTheme.of(context).bodyText1Family),
                                                                    ),
                                                              ),
                                                            ),
                                                            Padding(
                                                              padding:
                                                                  EdgeInsetsDirectional
                                                                      .fromSTEB(
                                                                          20,
                                                                          4,
                                                                          0,
                                                                          0),
                                                              child: Text(
                                                                dateTimeFormat(
                                                                    'yMMMd',
                                                                    getCurrentTimestamp),
                                                                style: FlutterFlowTheme.of(
                                                                        context)
                                                                    .bodyText2
                                                                    .override(
                                                                      fontFamily:
                                                                          FlutterFlowTheme.of(context)
                                                                              .bodyText2Family,
                                                                      color: FlutterFlowTheme.of(
                                                                              context)
                                                                          .primaryText,
                                                                      fontSize:
                                                                          12,
                                                                      fontWeight:
                                                                          FontWeight
                                                                              .w600,
                                                                      useGoogleFonts: GoogleFonts
                                                                              .asMap()
                                                                          .containsKey(
                                                                              FlutterFlowTheme.of(context).bodyText2Family),
                                                                    ),
                                                              ),
                                                            ),
                                                          ],
                                                        ),
                                                      ),
                                                      Padding(
                                                        padding:
                                                            EdgeInsetsDirectional
                                                                .fromSTEB(
                                                                    0, 0, 8, 0),
                                                        child: Column(
                                                          mainAxisSize:
                                                              MainAxisSize.max,
                                                          mainAxisAlignment:
                                                              MainAxisAlignment
                                                                  .center,
                                                          children: [],
                                                        ),
                                                      ),
                                                    ],
                                                  ),
                                                ),
                                              ),
                                            ),
                                            Padding(
                                              padding: EdgeInsetsDirectional
                                                  .fromSTEB(0, 16, 0, 0),
                                              child: Material(
                                                color: Colors.transparent,
                                                elevation: 0,
                                                shape: RoundedRectangleBorder(
                                                  borderRadius:
                                                      BorderRadius.circular(8),
                                                ),
                                                child: Container(
                                                  width: MediaQuery.of(context)
                                                          .size
                                                          .width *
                                                      0.9,
                                                  height: 60,
                                                  decoration: BoxDecoration(
                                                    boxShadow: [
                                                      BoxShadow(
                                                        blurRadius: 2,
                                                        color:
                                                            Color(0x33000000),
                                                        offset: Offset(0, 2),
                                                      )
                                                    ],
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                    border: Border.all(
                                                      width: 1,
                                                    ),
                                                  ),
                                                  child: TextFormField(
                                                    controller:
                                                        _model.weightController,
                                                    obscureText: false,
                                                    decoration: InputDecoration(
                                                      labelText:
                                                          'Body weight (kg)',
                                                      hintStyle:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .subtitle1
                                                              .override(
                                                                fontFamily: FlutterFlowTheme.of(
                                                                        context)
                                                                    .subtitle1Family,
                                                                color: FlutterFlowTheme.of(
                                                                        context)
                                                                    .primaryText,
                                                                useGoogleFonts: GoogleFonts
                                                                        .asMap()
                                                                    .containsKey(
                                                                        FlutterFlowTheme.of(context)
                                                                            .subtitle1Family),
                                                              ),
                                                      enabledBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      focusedBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      errorBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      focusedErrorBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      filled: true,
                                                    ),
                                                    style: FlutterFlowTheme.of(
                                                            context)
                                                        .bodyText1,
                                                    keyboardType:
                                                        TextInputType.number,
                                                    validator: _model
                                                        .weightControllerValidator
                                                        .asValidator(context),
                                                    inputFormatters: [
                                                      FilteringTextInputFormatter
                                                          .allow(
                                                              RegExp('[0-9]'))
                                                    ],
                                                  ),
                                                ),
                                              ),
                                            ),
                                            Padding(
                                              padding: EdgeInsetsDirectional
                                                  .fromSTEB(0, 16, 0, 0),
                                              child: Material(
                                                color: Colors.transparent,
                                                elevation: 0,
                                                shape: RoundedRectangleBorder(
                                                  borderRadius:
                                                      BorderRadius.circular(8),
                                                ),
                                                child: Container(
                                                  width: MediaQuery.of(context)
                                                          .size
                                                          .width *
                                                      0.9,
                                                  height: 60,
                                                  decoration: BoxDecoration(
                                                    boxShadow: [
                                                      BoxShadow(
                                                        blurRadius: 2,
                                                        color:
                                                            Color(0x33000000),
                                                        offset: Offset(0, 2),
                                                      )
                                                    ],
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                    border: Border.all(
                                                      width: 1,
                                                    ),
                                                  ),
                                                  child: TextFormField(
                                                    controller: _model
                                                        .systolicController,
                                                    obscureText: false,
                                                    decoration: InputDecoration(
                                                      labelText:
                                                          'BP (Systolic)mmHg',
                                                      hintStyle:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .subtitle1
                                                              .override(
                                                                fontFamily: FlutterFlowTheme.of(
                                                                        context)
                                                                    .subtitle1Family,
                                                                color: FlutterFlowTheme.of(
                                                                        context)
                                                                    .primaryText,
                                                                useGoogleFonts: GoogleFonts
                                                                        .asMap()
                                                                    .containsKey(
                                                                        FlutterFlowTheme.of(context)
                                                                            .subtitle1Family),
                                                              ),
                                                      enabledBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      focusedBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      errorBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      focusedErrorBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      filled: true,
                                                    ),
                                                    style: FlutterFlowTheme.of(
                                                            context)
                                                        .bodyText1,
                                                    keyboardType:
                                                        TextInputType.number,
                                                    validator: _model
                                                        .systolicControllerValidator
                                                        .asValidator(context),
                                                    inputFormatters: [
                                                      FilteringTextInputFormatter
                                                          .allow(
                                                              RegExp('[0-9]'))
                                                    ],
                                                  ),
                                                ),
                                              ),
                                            ),
                                            Padding(
                                              padding: EdgeInsetsDirectional
                                                  .fromSTEB(0, 16, 0, 0),
                                              child: Material(
                                                color: Colors.transparent,
                                                elevation: 0,
                                                shape: RoundedRectangleBorder(
                                                  borderRadius:
                                                      BorderRadius.circular(8),
                                                ),
                                                child: Container(
                                                  width: MediaQuery.of(context)
                                                          .size
                                                          .width *
                                                      0.9,
                                                  height: 60,
                                                  decoration: BoxDecoration(
                                                    boxShadow: [
                                                      BoxShadow(
                                                        blurRadius: 2,
                                                        color:
                                                            Color(0x33000000),
                                                        offset: Offset(0, 2),
                                                      )
                                                    ],
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                    border: Border.all(
                                                      width: 1,
                                                    ),
                                                  ),
                                                  child: TextFormField(
                                                    controller: _model
                                                        .diastolicController,
                                                    obscureText: false,
                                                    decoration: InputDecoration(
                                                      labelText:
                                                          'BP (Diastolic)mmHg',
                                                      hintStyle:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .subtitle1
                                                              .override(
                                                                fontFamily: FlutterFlowTheme.of(
                                                                        context)
                                                                    .subtitle1Family,
                                                                color: FlutterFlowTheme.of(
                                                                        context)
                                                                    .primaryText,
                                                                useGoogleFonts: GoogleFonts
                                                                        .asMap()
                                                                    .containsKey(
                                                                        FlutterFlowTheme.of(context)
                                                                            .subtitle1Family),
                                                              ),
                                                      enabledBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      focusedBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      errorBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      focusedErrorBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      filled: true,
                                                    ),
                                                    style: FlutterFlowTheme.of(
                                                            context)
                                                        .bodyText1,
                                                    keyboardType:
                                                        TextInputType.number,
                                                    validator: _model
                                                        .diastolicControllerValidator
                                                        .asValidator(context),
                                                    inputFormatters: [
                                                      FilteringTextInputFormatter
                                                          .allow(
                                                              RegExp('[0-9]'))
                                                    ],
                                                  ),
                                                ),
                                              ),
                                            ),
                                            Padding(
                                              padding: EdgeInsetsDirectional
                                                  .fromSTEB(0, 16, 0, 0),
                                              child: Material(
                                                color: Colors.transparent,
                                                elevation: 0,
                                                shape: RoundedRectangleBorder(
                                                  borderRadius:
                                                      BorderRadius.circular(8),
                                                ),
                                                child: Container(
                                                  width: MediaQuery.of(context)
                                                          .size
                                                          .width *
                                                      0.9,
                                                  height: 60,
                                                  decoration: BoxDecoration(
                                                    boxShadow: [
                                                      BoxShadow(
                                                        blurRadius: 2,
                                                        color:
                                                            Color(0x33000000),
                                                        offset: Offset(0, 2),
                                                      )
                                                    ],
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                    border: Border.all(
                                                      width: 1,
                                                    ),
                                                  ),
                                                  child: TextFormField(
                                                    controller: _model
                                                        .temperatureController,
                                                    obscureText: false,
                                                    decoration: InputDecoration(
                                                      labelText:
                                                          'Temperature (C)',
                                                      hintStyle:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .subtitle1
                                                              .override(
                                                                fontFamily: FlutterFlowTheme.of(
                                                                        context)
                                                                    .subtitle1Family,
                                                                color: FlutterFlowTheme.of(
                                                                        context)
                                                                    .primaryText,
                                                                useGoogleFonts: GoogleFonts
                                                                        .asMap()
                                                                    .containsKey(
                                                                        FlutterFlowTheme.of(context)
                                                                            .subtitle1Family),
                                                              ),
                                                      enabledBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      focusedBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      errorBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      focusedErrorBorder:
                                                          OutlineInputBorder(
                                                        borderSide: BorderSide(
                                                          color:
                                                              Color(0x00000000),
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            const BorderRadius
                                                                .only(
                                                          topLeft:
                                                              Radius.circular(
                                                                  4.0),
                                                          topRight:
                                                              Radius.circular(
                                                                  4.0),
                                                        ),
                                                      ),
                                                      filled: true,
                                                    ),
                                                    style: FlutterFlowTheme.of(
                                                            context)
                                                        .bodyText1,
                                                    keyboardType:
                                                        TextInputType.number,
                                                    validator: _model
                                                        .temperatureControllerValidator
                                                        .asValidator(context),
                                                    inputFormatters: [
                                                      FilteringTextInputFormatter
                                                          .allow(
                                                              RegExp('[0-9]'))
                                                    ],
                                                  ),
                                                ),
                                              ),
                                            ),
                                          ],
                                        ),
                                      ),
                                    ),
                                  ),
                                  Padding(
                                    padding: EdgeInsetsDirectional.fromSTEB(
                                        0, 40, 0, 0),
                                    child: Card(
                                      clipBehavior: Clip.antiAliasWithSaveLayer,
                                      color:
                                          FlutterFlowTheme.of(context).overlay,
                                      shape: RoundedRectangleBorder(
                                        borderRadius: BorderRadius.circular(5),
                                      ),
                                      child: Padding(
                                        padding: EdgeInsetsDirectional.fromSTEB(
                                            10, 10, 10, 10),
                                        child: SingleChildScrollView(
                                          child: Column(
                                            mainAxisSize: MainAxisSize.max,
                                            children: [
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 10, 0, 0),
                                                child: Row(
                                                  mainAxisSize:
                                                      MainAxisSize.max,
                                                  mainAxisAlignment:
                                                      MainAxisAlignment.center,
                                                  children: [
                                                    Text(
                                                      'TB Screening',
                                                      style:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .title2,
                                                    ),
                                                  ],
                                                ),
                                              ),
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 16, 0, 0),
                                                child: Material(
                                                  color: Colors.transparent,
                                                  elevation: 0,
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                  ),
                                                  child: Container(
                                                    width: double.infinity,
                                                    height: 60,
                                                    decoration: BoxDecoration(
                                                      boxShadow: [
                                                        BoxShadow(
                                                          blurRadius: 2,
                                                          color:
                                                              Color(0x33000000),
                                                          offset: Offset(0, 2),
                                                        )
                                                      ],
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              8),
                                                      border: Border.all(
                                                        width: 1,
                                                      ),
                                                    ),
                                                    child: Padding(
                                                      padding:
                                                          EdgeInsetsDirectional
                                                              .fromSTEB(
                                                                  5, 0, 0, 0),
                                                      child: Column(
                                                        mainAxisSize:
                                                            MainAxisSize.max,
                                                        crossAxisAlignment:
                                                            CrossAxisAlignment
                                                                .start,
                                                        children: [
                                                          Text(
                                                            'Have you been coughing for 2 weeks or more?',
                                                            style: FlutterFlowTheme
                                                                    .of(context)
                                                                .subtitle1
                                                                .override(
                                                                  fontFamily: FlutterFlowTheme.of(
                                                                          context)
                                                                      .subtitle1Family,
                                                                  fontSize: 15,
                                                                  useGoogleFonts: GoogleFonts
                                                                          .asMap()
                                                                      .containsKey(
                                                                          FlutterFlowTheme.of(context)
                                                                              .subtitle1Family),
                                                                ),
                                                          ),
                                                          Expanded(
                                                            child:
                                                                FlutterFlowDropDown<
                                                                    String>(
                                                              options: [
                                                                'Yes',
                                                                'No'
                                                              ],
                                                              onChanged: (val) =>
                                                                  setState(() =>
                                                                      _model.caughingValue =
                                                                          val),
                                                              width: double
                                                                  .infinity,
                                                              height: 50,
                                                              textStyle:
                                                                  FlutterFlowTheme.of(
                                                                          context)
                                                                      .bodyText1
                                                                      .override(
                                                                        fontFamily:
                                                                            FlutterFlowTheme.of(context).bodyText1Family,
                                                                        color: FlutterFlowTheme.of(context)
                                                                            .primaryText,
                                                                        useGoogleFonts:
                                                                            GoogleFonts.asMap().containsKey(FlutterFlowTheme.of(context).bodyText1Family),
                                                                      ),
                                                              elevation: 2,
                                                              borderColor: Colors
                                                                  .transparent,
                                                              borderWidth: 0,
                                                              borderRadius: 0,
                                                              margin:
                                                                  EdgeInsetsDirectional
                                                                      .fromSTEB(
                                                                          12,
                                                                          4,
                                                                          12,
                                                                          4),
                                                              hidesUnderline:
                                                                  true,
                                                            ),
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                  ),
                                                ),
                                              ),
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 16, 0, 0),
                                                child: Material(
                                                  color: Colors.transparent,
                                                  elevation: 0,
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                  ),
                                                  child: Container(
                                                    width: double.infinity,
                                                    height: 60,
                                                    decoration: BoxDecoration(
                                                      boxShadow: [
                                                        BoxShadow(
                                                          blurRadius: 2,
                                                          color:
                                                              Color(0x33000000),
                                                          offset: Offset(0, 2),
                                                        )
                                                      ],
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              8),
                                                      border: Border.all(
                                                        width: 1,
                                                      ),
                                                    ),
                                                    child: Padding(
                                                      padding:
                                                          EdgeInsetsDirectional
                                                              .fromSTEB(
                                                                  5, 0, 0, 0),
                                                      child: Column(
                                                        mainAxisSize:
                                                            MainAxisSize.max,
                                                        crossAxisAlignment:
                                                            CrossAxisAlignment
                                                                .start,
                                                        children: [
                                                          Text(
                                                            'Do you have fever for 2 weeks or more?',
                                                            style: FlutterFlowTheme
                                                                    .of(context)
                                                                .subtitle1
                                                                .override(
                                                                  fontFamily: FlutterFlowTheme.of(
                                                                          context)
                                                                      .subtitle1Family,
                                                                  fontSize: 15,
                                                                  useGoogleFonts: GoogleFonts
                                                                          .asMap()
                                                                      .containsKey(
                                                                          FlutterFlowTheme.of(context)
                                                                              .subtitle1Family),
                                                                ),
                                                          ),
                                                          Expanded(
                                                            child:
                                                                FlutterFlowDropDown<
                                                                    String>(
                                                              options: [
                                                                'Yes',
                                                                'No'
                                                              ],
                                                              onChanged: (val) =>
                                                                  setState(() =>
                                                                      _model.feverValue =
                                                                          val),
                                                              width: double
                                                                  .infinity,
                                                              height: 50,
                                                              textStyle:
                                                                  FlutterFlowTheme.of(
                                                                          context)
                                                                      .bodyText1
                                                                      .override(
                                                                        fontFamily:
                                                                            FlutterFlowTheme.of(context).bodyText1Family,
                                                                        color: FlutterFlowTheme.of(context)
                                                                            .primaryText,
                                                                        useGoogleFonts:
                                                                            GoogleFonts.asMap().containsKey(FlutterFlowTheme.of(context).bodyText1Family),
                                                                      ),
                                                              elevation: 2,
                                                              borderColor: Colors
                                                                  .transparent,
                                                              borderWidth: 0,
                                                              borderRadius: 0,
                                                              margin:
                                                                  EdgeInsetsDirectional
                                                                      .fromSTEB(
                                                                          12,
                                                                          4,
                                                                          12,
                                                                          4),
                                                              hidesUnderline:
                                                                  true,
                                                            ),
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                  ),
                                                ),
                                              ),
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 16, 0, 0),
                                                child: Material(
                                                  color: Colors.transparent,
                                                  elevation: 0,
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                  ),
                                                  child: Container(
                                                    width: double.infinity,
                                                    height: 60,
                                                    decoration: BoxDecoration(
                                                      boxShadow: [
                                                        BoxShadow(
                                                          blurRadius: 2,
                                                          color:
                                                              Color(0x33000000),
                                                          offset: Offset(0, 2),
                                                        )
                                                      ],
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              8),
                                                      border: Border.all(
                                                        width: 1,
                                                      ),
                                                    ),
                                                    child: Padding(
                                                      padding:
                                                          EdgeInsetsDirectional
                                                              .fromSTEB(
                                                                  5, 0, 0, 0),
                                                      child: Column(
                                                        mainAxisSize:
                                                            MainAxisSize.max,
                                                        crossAxisAlignment:
                                                            CrossAxisAlignment
                                                                .start,
                                                        children: [
                                                          Text(
                                                            'Are you loosng weight?',
                                                            style: FlutterFlowTheme
                                                                    .of(context)
                                                                .subtitle1
                                                                .override(
                                                                  fontFamily: FlutterFlowTheme.of(
                                                                          context)
                                                                      .subtitle1Family,
                                                                  fontSize: 15,
                                                                  useGoogleFonts: GoogleFonts
                                                                          .asMap()
                                                                      .containsKey(
                                                                          FlutterFlowTheme.of(context)
                                                                              .subtitle1Family),
                                                                ),
                                                          ),
                                                          Expanded(
                                                            child:
                                                                FlutterFlowDropDown<
                                                                    String>(
                                                              options: [
                                                                'Yes',
                                                                'No'
                                                              ],
                                                              onChanged: (val) =>
                                                                  setState(() =>
                                                                      _model.weightLossValue =
                                                                          val),
                                                              width: double
                                                                  .infinity,
                                                              height: 50,
                                                              textStyle:
                                                                  FlutterFlowTheme.of(
                                                                          context)
                                                                      .bodyText1
                                                                      .override(
                                                                        fontFamily:
                                                                            FlutterFlowTheme.of(context).bodyText1Family,
                                                                        color: FlutterFlowTheme.of(context)
                                                                            .primaryText,
                                                                        useGoogleFonts:
                                                                            GoogleFonts.asMap().containsKey(FlutterFlowTheme.of(context).bodyText1Family),
                                                                      ),
                                                              elevation: 2,
                                                              borderColor: Colors
                                                                  .transparent,
                                                              borderWidth: 0,
                                                              borderRadius: 0,
                                                              margin:
                                                                  EdgeInsetsDirectional
                                                                      .fromSTEB(
                                                                          12,
                                                                          4,
                                                                          12,
                                                                          4),
                                                              hidesUnderline:
                                                                  true,
                                                            ),
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                  ),
                                                ),
                                              ),
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 16, 0, 0),
                                                child: Material(
                                                  color: Colors.transparent,
                                                  elevation: 0,
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                  ),
                                                  child: Container(
                                                    width: double.infinity,
                                                    height: 60,
                                                    decoration: BoxDecoration(
                                                      boxShadow: [
                                                        BoxShadow(
                                                          blurRadius: 2,
                                                          color:
                                                              Color(0x33000000),
                                                          offset: Offset(0, 2),
                                                        )
                                                      ],
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              8),
                                                      border: Border.all(
                                                        width: 1,
                                                      ),
                                                    ),
                                                    child: Padding(
                                                      padding:
                                                          EdgeInsetsDirectional
                                                              .fromSTEB(
                                                                  5, 0, 0, 0),
                                                      child: Column(
                                                        mainAxisSize:
                                                            MainAxisSize.max,
                                                        crossAxisAlignment:
                                                            CrossAxisAlignment
                                                                .start,
                                                        children: [
                                                          Text(
                                                            'Are you having night sweats?',
                                                            style: FlutterFlowTheme
                                                                    .of(context)
                                                                .subtitle1
                                                                .override(
                                                                  fontFamily: FlutterFlowTheme.of(
                                                                          context)
                                                                      .subtitle1Family,
                                                                  fontSize: 15,
                                                                  useGoogleFonts: GoogleFonts
                                                                          .asMap()
                                                                      .containsKey(
                                                                          FlutterFlowTheme.of(context)
                                                                              .subtitle1Family),
                                                                ),
                                                          ),
                                                          Expanded(
                                                            child:
                                                                FlutterFlowDropDown<
                                                                    String>(
                                                              options: [
                                                                'Yes',
                                                                'No'
                                                              ],
                                                              onChanged: (val) =>
                                                                  setState(() =>
                                                                      _model.nightSweatValue =
                                                                          val),
                                                              width: double
                                                                  .infinity,
                                                              height: 50,
                                                              textStyle:
                                                                  FlutterFlowTheme.of(
                                                                          context)
                                                                      .bodyText1
                                                                      .override(
                                                                        fontFamily:
                                                                            FlutterFlowTheme.of(context).bodyText1Family,
                                                                        color: FlutterFlowTheme.of(context)
                                                                            .primaryText,
                                                                        useGoogleFonts:
                                                                            GoogleFonts.asMap().containsKey(FlutterFlowTheme.of(context).bodyText1Family),
                                                                      ),
                                                              elevation: 2,
                                                              borderColor: Colors
                                                                  .transparent,
                                                              borderWidth: 0,
                                                              borderRadius: 0,
                                                              margin:
                                                                  EdgeInsetsDirectional
                                                                      .fromSTEB(
                                                                          12,
                                                                          4,
                                                                          12,
                                                                          4),
                                                              hidesUnderline:
                                                                  true,
                                                            ),
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                  ),
                                                ),
                                              ),
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 16, 0, 0),
                                                child: Material(
                                                  color: Colors.transparent,
                                                  elevation: 0,
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                  ),
                                                  child: Container(
                                                    width: double.infinity,
                                                    height: 60,
                                                    decoration: BoxDecoration(
                                                      boxShadow: [
                                                        BoxShadow(
                                                          blurRadius: 2,
                                                          color:
                                                              Color(0x33000000),
                                                          offset: Offset(0, 2),
                                                        )
                                                      ],
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              8),
                                                      border: Border.all(
                                                        width: 1,
                                                      ),
                                                    ),
                                                    child: Padding(
                                                      padding:
                                                          EdgeInsetsDirectional
                                                              .fromSTEB(
                                                                  5, 0, 0, 0),
                                                      child: Column(
                                                        mainAxisSize:
                                                            MainAxisSize.max,
                                                        crossAxisAlignment:
                                                            CrossAxisAlignment
                                                                .start,
                                                        children: [
                                                          Text(
                                                            'Do you have any swellings in the neck or armpit?',
                                                            style: FlutterFlowTheme
                                                                    .of(context)
                                                                .subtitle1
                                                                .override(
                                                                  fontFamily: FlutterFlowTheme.of(
                                                                          context)
                                                                      .subtitle1Family,
                                                                  fontSize: 15,
                                                                  useGoogleFonts: GoogleFonts
                                                                          .asMap()
                                                                      .containsKey(
                                                                          FlutterFlowTheme.of(context)
                                                                              .subtitle1Family),
                                                                ),
                                                          ),
                                                          Expanded(
                                                            child:
                                                                FlutterFlowDropDown<
                                                                    String>(
                                                              options: [
                                                                'Yes',
                                                                'No'
                                                              ],
                                                              onChanged: (val) =>
                                                                  setState(() =>
                                                                      _model.swellingsValue =
                                                                          val),
                                                              width: double
                                                                  .infinity,
                                                              height: 50,
                                                              textStyle:
                                                                  FlutterFlowTheme.of(
                                                                          context)
                                                                      .bodyText1
                                                                      .override(
                                                                        fontFamily:
                                                                            FlutterFlowTheme.of(context).bodyText1Family,
                                                                        color: FlutterFlowTheme.of(context)
                                                                            .primaryText,
                                                                        useGoogleFonts:
                                                                            GoogleFonts.asMap().containsKey(FlutterFlowTheme.of(context).bodyText1Family),
                                                                      ),
                                                              elevation: 2,
                                                              borderColor: Colors
                                                                  .transparent,
                                                              borderWidth: 0,
                                                              borderRadius: 0,
                                                              margin:
                                                                  EdgeInsetsDirectional
                                                                      .fromSTEB(
                                                                          12,
                                                                          4,
                                                                          12,
                                                                          4),
                                                              hidesUnderline:
                                                                  true,
                                                            ),
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                  ),
                                                ),
                                              ),
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 16, 0, 0),
                                                child: Material(
                                                  color: Colors.transparent,
                                                  elevation: 0,
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                  ),
                                                  child: Container(
                                                    width: double.infinity,
                                                    height: 60,
                                                    decoration: BoxDecoration(
                                                      boxShadow: [
                                                        BoxShadow(
                                                          blurRadius: 2,
                                                          color:
                                                              Color(0x33000000),
                                                          offset: Offset(0, 2),
                                                        )
                                                      ],
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              8),
                                                      border: Border.all(
                                                        width: 1,
                                                      ),
                                                    ),
                                                    child: Padding(
                                                      padding:
                                                          EdgeInsetsDirectional
                                                              .fromSTEB(
                                                                  5, 0, 0, 0),
                                                      child: Column(
                                                        mainAxisSize:
                                                            MainAxisSize.max,
                                                        crossAxisAlignment:
                                                            CrossAxisAlignment
                                                                .start,
                                                        children: [
                                                          Text(
                                                            'Refer client for TB diagnosis?',
                                                            style: FlutterFlowTheme
                                                                    .of(context)
                                                                .subtitle1
                                                                .override(
                                                                  fontFamily: FlutterFlowTheme.of(
                                                                          context)
                                                                      .subtitle1Family,
                                                                  fontSize: 15,
                                                                  useGoogleFonts: GoogleFonts
                                                                          .asMap()
                                                                      .containsKey(
                                                                          FlutterFlowTheme.of(context)
                                                                              .subtitle1Family),
                                                                ),
                                                          ),
                                                          Expanded(
                                                            child:
                                                                FlutterFlowDropDown<
                                                                    String>(
                                                              options: [
                                                                'Yes',
                                                                'No'
                                                              ],
                                                              onChanged: (val) =>
                                                                  setState(() =>
                                                                      _model.tbReferValue =
                                                                          val),
                                                              width: double
                                                                  .infinity,
                                                              height: 50,
                                                              textStyle:
                                                                  FlutterFlowTheme.of(
                                                                          context)
                                                                      .bodyText1
                                                                      .override(
                                                                        fontFamily:
                                                                            FlutterFlowTheme.of(context).bodyText1Family,
                                                                        color: FlutterFlowTheme.of(context)
                                                                            .primaryText,
                                                                        useGoogleFonts:
                                                                            GoogleFonts.asMap().containsKey(FlutterFlowTheme.of(context).bodyText1Family),
                                                                      ),
                                                              elevation: 2,
                                                              borderColor: Colors
                                                                  .transparent,
                                                              borderWidth: 0,
                                                              borderRadius: 0,
                                                              margin:
                                                                  EdgeInsetsDirectional
                                                                      .fromSTEB(
                                                                          12,
                                                                          4,
                                                                          12,
                                                                          4),
                                                              hidesUnderline:
                                                                  true,
                                                            ),
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                  ),
                                                ),
                                              ),
                                            ],
                                          ),
                                        ),
                                      ),
                                    ),
                                  ),
                                  Padding(
                                    padding: EdgeInsetsDirectional.fromSTEB(
                                        0, 40, 0, 0),
                                    child: Card(
                                      clipBehavior: Clip.antiAliasWithSaveLayer,
                                      color:
                                          FlutterFlowTheme.of(context).overlay,
                                      shape: RoundedRectangleBorder(
                                        borderRadius: BorderRadius.circular(5),
                                      ),
                                      child: Padding(
                                        padding: EdgeInsetsDirectional.fromSTEB(
                                            10, 10, 10, 10),
                                        child: SingleChildScrollView(
                                          child: Column(
                                            mainAxisSize: MainAxisSize.max,
                                            children: [
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 10, 0, 0),
                                                child: Row(
                                                  mainAxisSize:
                                                      MainAxisSize.max,
                                                  mainAxisAlignment:
                                                      MainAxisAlignment.center,
                                                  children: [
                                                    Text(
                                                      'Drug Refill',
                                                      style:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .title2,
                                                    ),
                                                  ],
                                                ),
                                              ),
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 16, 0, 0),
                                                child: Material(
                                                  color: Colors.transparent,
                                                  elevation: 0,
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                  ),
                                                  child: Container(
                                                    width: double.infinity,
                                                    height: 60,
                                                    decoration: BoxDecoration(
                                                      boxShadow: [
                                                        BoxShadow(
                                                          blurRadius: 2,
                                                          color:
                                                              Color(0x33000000),
                                                          offset: Offset(0, 2),
                                                        )
                                                      ],
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              8),
                                                      border: Border.all(
                                                        width: 1,
                                                      ),
                                                    ),
                                                    child: Padding(
                                                      padding:
                                                          EdgeInsetsDirectional
                                                              .fromSTEB(
                                                                  5, 0, 0, 0),
                                                      child: Column(
                                                        mainAxisSize:
                                                            MainAxisSize.max,
                                                        crossAxisAlignment:
                                                            CrossAxisAlignment
                                                                .start,
                                                        children: [
                                                          Padding(
                                                            padding:
                                                                EdgeInsetsDirectional
                                                                    .fromSTEB(
                                                                        10,
                                                                        0,
                                                                        0,
                                                                        0),
                                                            child: Text(
                                                              'Regimen',
                                                              style: FlutterFlowTheme
                                                                      .of(context)
                                                                  .subtitle1
                                                                  .override(
                                                                    fontFamily:
                                                                        FlutterFlowTheme.of(context)
                                                                            .subtitle1Family,
                                                                    fontSize:
                                                                        15,
                                                                    useGoogleFonts: GoogleFonts
                                                                            .asMap()
                                                                        .containsKey(
                                                                            FlutterFlowTheme.of(context).subtitle1Family),
                                                                  ),
                                                            ),
                                                          ),
                                                          Padding(
                                                            padding:
                                                                EdgeInsetsDirectional
                                                                    .fromSTEB(
                                                                        5,
                                                                        0,
                                                                        0,
                                                                        0),
                                                            child: Text(
                                                              _model.patient
                                                                      ?.assignedRegimen ??
                                                                  '',
                                                              style: FlutterFlowTheme
                                                                      .of(context)
                                                                  .bodyText1,
                                                            ),
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                  ),
                                                ),
                                              ),
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 16, 0, 0),
                                                child: Material(
                                                  color: Colors.transparent,
                                                  elevation: 0,
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                  ),
                                                  child: Container(
                                                    width: double.infinity,
                                                    height: 60,
                                                    decoration: BoxDecoration(
                                                      boxShadow: [
                                                        BoxShadow(
                                                          blurRadius: 2,
                                                          color:
                                                              Color(0x33000000),
                                                          offset: Offset(0, 2),
                                                        )
                                                      ],
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              8),
                                                      border: Border.all(
                                                        width: 1,
                                                      ),
                                                    ),
                                                    child: TextFormField(
                                                      controller: _model
                                                          .qtyPrescribedController,
                                                      obscureText: false,
                                                      decoration:
                                                          InputDecoration(
                                                        labelText:
                                                            'Quantity Prescribed',
                                                        hintStyle:
                                                            FlutterFlowTheme.of(
                                                                    context)
                                                                .title2,
                                                        enabledBorder:
                                                            OutlineInputBorder(
                                                          borderSide:
                                                              BorderSide(
                                                            color: Color(
                                                                0x00000000),
                                                            width: 1,
                                                          ),
                                                          borderRadius:
                                                              const BorderRadius
                                                                  .only(
                                                            topLeft:
                                                                Radius.circular(
                                                                    4.0),
                                                            topRight:
                                                                Radius.circular(
                                                                    4.0),
                                                          ),
                                                        ),
                                                        focusedBorder:
                                                            OutlineInputBorder(
                                                          borderSide:
                                                              BorderSide(
                                                            color: Color(
                                                                0x00000000),
                                                            width: 1,
                                                          ),
                                                          borderRadius:
                                                              const BorderRadius
                                                                  .only(
                                                            topLeft:
                                                                Radius.circular(
                                                                    4.0),
                                                            topRight:
                                                                Radius.circular(
                                                                    4.0),
                                                          ),
                                                        ),
                                                        errorBorder:
                                                            OutlineInputBorder(
                                                          borderSide:
                                                              BorderSide(
                                                            color: Color(
                                                                0x00000000),
                                                            width: 1,
                                                          ),
                                                          borderRadius:
                                                              const BorderRadius
                                                                  .only(
                                                            topLeft:
                                                                Radius.circular(
                                                                    4.0),
                                                            topRight:
                                                                Radius.circular(
                                                                    4.0),
                                                          ),
                                                        ),
                                                        focusedErrorBorder:
                                                            OutlineInputBorder(
                                                          borderSide:
                                                              BorderSide(
                                                            color: Color(
                                                                0x00000000),
                                                            width: 1,
                                                          ),
                                                          borderRadius:
                                                              const BorderRadius
                                                                  .only(
                                                            topLeft:
                                                                Radius.circular(
                                                                    4.0),
                                                            topRight:
                                                                Radius.circular(
                                                                    4.0),
                                                          ),
                                                        ),
                                                      ),
                                                      style:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .bodyText1,
                                                      keyboardType:
                                                          TextInputType.number,
                                                      validator: _model
                                                          .qtyPrescribedControllerValidator
                                                          .asValidator(context),
                                                    ),
                                                  ),
                                                ),
                                              ),
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 16, 0, 0),
                                                child: Material(
                                                  color: Colors.transparent,
                                                  elevation: 0,
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                  ),
                                                  child: Container(
                                                    width: double.infinity,
                                                    height: 60,
                                                    decoration: BoxDecoration(
                                                      boxShadow: [
                                                        BoxShadow(
                                                          blurRadius: 2,
                                                          color:
                                                              Color(0x33000000),
                                                          offset: Offset(0, 2),
                                                        )
                                                      ],
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              8),
                                                      border: Border.all(
                                                        width: 1,
                                                      ),
                                                    ),
                                                    child: TextFormField(
                                                      controller: _model
                                                          .qtyDispensedController,
                                                      onChanged: (_) =>
                                                          EasyDebounce.debounce(
                                                        '_model.qtyDispensedController',
                                                        Duration(
                                                            milliseconds: 2000),
                                                        () async {
                                                          setState(() {
                                                            DateTime nrd = DateTime(
                                                                getCurrentTimestamp
                                                                    .year,
                                                                getCurrentTimestamp
                                                                    .month,
                                                                getCurrentTimestamp
                                                                        .day +
                                                                    (int.tryParse(_model
                                                                            .qtyDispensedController
                                                                            .text) ??
                                                                        0));
                                                            _model.datePicked =
                                                                nrd;
                                                          });
                                                        },
                                                      ),
                                                      obscureText: false,
                                                      decoration:
                                                          InputDecoration(
                                                        labelText:
                                                            'Quantity Dispensed',
                                                        hintStyle:
                                                            FlutterFlowTheme.of(
                                                                    context)
                                                                .bodyText1,
                                                        enabledBorder:
                                                            OutlineInputBorder(
                                                          borderSide:
                                                              BorderSide(
                                                            color: Color(
                                                                0x00000000),
                                                            width: 1,
                                                          ),
                                                          borderRadius:
                                                              const BorderRadius
                                                                  .only(
                                                            topLeft:
                                                                Radius.circular(
                                                                    4.0),
                                                            topRight:
                                                                Radius.circular(
                                                                    4.0),
                                                          ),
                                                        ),
                                                        focusedBorder:
                                                            OutlineInputBorder(
                                                          borderSide:
                                                              BorderSide(
                                                            color: Color(
                                                                0x00000000),
                                                            width: 1,
                                                          ),
                                                          borderRadius:
                                                              const BorderRadius
                                                                  .only(
                                                            topLeft:
                                                                Radius.circular(
                                                                    4.0),
                                                            topRight:
                                                                Radius.circular(
                                                                    4.0),
                                                          ),
                                                        ),
                                                        errorBorder:
                                                            OutlineInputBorder(
                                                          borderSide:
                                                              BorderSide(
                                                            color: Color(
                                                                0x00000000),
                                                            width: 1,
                                                          ),
                                                          borderRadius:
                                                              const BorderRadius
                                                                  .only(
                                                            topLeft:
                                                                Radius.circular(
                                                                    4.0),
                                                            topRight:
                                                                Radius.circular(
                                                                    4.0),
                                                          ),
                                                        ),
                                                        focusedErrorBorder:
                                                            OutlineInputBorder(
                                                          borderSide:
                                                              BorderSide(
                                                            color: Color(
                                                                0x00000000),
                                                            width: 1,
                                                          ),
                                                          borderRadius:
                                                              const BorderRadius
                                                                  .only(
                                                            topLeft:
                                                                Radius.circular(
                                                                    4.0),
                                                            topRight:
                                                                Radius.circular(
                                                                    4.0),
                                                          ),
                                                        ),
                                                      ),
                                                      style:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .bodyText1,
                                                      keyboardType:
                                                          TextInputType.number,
                                                      validator: _model
                                                          .qtyDispensedControllerValidator
                                                          .asValidator(context),
                                                    ),
                                                  ),
                                                ),
                                              ),
                                              if (int.tryParse(_model
                                                          .qtyDispensedController
                                                          .text) !=
                                                      null &&
                                                  int.parse(_model
                                                          .qtyDispensedController
                                                          .text) >
                                                      FFAppState().regimenQty)
                                                Row(
                                                  mainAxisSize:
                                                      MainAxisSize.max,
                                                  children: [
                                                    Text(
                                                      'Not enough quantity to dispense',
                                                      style:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .bodyText1
                                                              .override(
                                                                fontFamily: FlutterFlowTheme.of(
                                                                        context)
                                                                    .bodyText1Family,
                                                                color: Color(
                                                                    0xFF965600),
                                                                useGoogleFonts: GoogleFonts
                                                                        .asMap()
                                                                    .containsKey(
                                                                        FlutterFlowTheme.of(context)
                                                                            .bodyText1Family),
                                                              ),
                                                    ),
                                                  ],
                                                ),
                                              if (nextVisitIsAfterNextCancerScreeningAppointment())
                                                Row(
                                                  mainAxisSize:
                                                      MainAxisSize.max,
                                                  children: [
                                                    Text(
                                                      'Next appointment is after a facility appointment for Cervical cancer screening',
                                                      style:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .bodyText1
                                                              .override(
                                                                fontFamily: FlutterFlowTheme.of(
                                                                        context)
                                                                    .bodyText1Family,
                                                                color: Color(
                                                                    0xFF8D2626),
                                                                useGoogleFonts: GoogleFonts
                                                                        .asMap()
                                                                    .containsKey(
                                                                        FlutterFlowTheme.of(context)
                                                                            .bodyText1Family),
                                                              ),
                                                    ),
                                                  ],
                                                ),
                                              if (nextVisitIsAfterNextTPTAppointment())
                                                Row(
                                                  mainAxisSize:
                                                      MainAxisSize.max,
                                                  children: [
                                                    Text(
                                                      'Next appointment is after a facility appointment for TPT',
                                                      style:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .bodyText1
                                                              .override(
                                                                fontFamily: FlutterFlowTheme.of(
                                                                        context)
                                                                    .bodyText1Family,
                                                                color: Color(
                                                                    0xFF8D2626),
                                                                useGoogleFonts: GoogleFonts
                                                                        .asMap()
                                                                    .containsKey(
                                                                        FlutterFlowTheme.of(context)
                                                                            .bodyText1Family),
                                                              ),
                                                    ),
                                                  ],
                                                ),
                                              if (nextVisitIsAfterNextVLAppointment())
                                                Row(
                                                  mainAxisSize:
                                                      MainAxisSize.max,
                                                  children: [
                                                    Text(
                                                      'Next appointment is after a facility appointment for Viral Load check',
                                                      style:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .bodyText1
                                                              .override(
                                                                fontFamily: FlutterFlowTheme.of(
                                                                        context)
                                                                    .bodyText1Family,
                                                                color: Color(
                                                                    0xFF8D2626),
                                                                useGoogleFonts: GoogleFonts
                                                                        .asMap()
                                                                    .containsKey(
                                                                        FlutterFlowTheme.of(context)
                                                                            .bodyText1Family),
                                                              ),
                                                    ),
                                                  ],
                                                ),
                                              Row(
                                                mainAxisSize: MainAxisSize.max,
                                                children: [
                                                  Text(
                                                    'Barcode:',
                                                    style: FlutterFlowTheme.of(
                                                            context)
                                                        .bodyText1,
                                                  ),
                                                  Expanded(
                                                    child: Padding(
                                                      padding:
                                                          EdgeInsetsDirectional
                                                              .fromSTEB(
                                                                  10, 0, 0, 0),
                                                      child: Row(
                                                        mainAxisSize:
                                                            MainAxisSize.max,
                                                        mainAxisAlignment:
                                                            MainAxisAlignment
                                                                .spaceBetween,
                                                        children: [
                                                          Text(
                                                            _model.barcode!,
                                                            style: FlutterFlowTheme
                                                                    .of(context)
                                                                .bodyText1,
                                                          ),
                                                          InkWell(
                                                            onTap: () async {
                                                              _model.barcode =
                                                                  await FlutterBarcodeScanner
                                                                      .scanBarcode(
                                                                '#C62828',
                                                                // scanning line color
                                                                'Cancel',
                                                                // cancel button text
                                                                true,
                                                                // whether to show the flash icon
                                                                ScanMode.QR,
                                                              );

                                                              await actions
                                                                  .verifyBarcode(
                                                                'verified',
                                                              );

                                                              setState(() {});
                                                            },
                                                            child: Icon(
                                                              Icons.scanner,
                                                              color: FlutterFlowTheme
                                                                      .of(context)
                                                                  .primaryText,
                                                              size: 32,
                                                            ),
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                  ),
                                                ],
                                              ),
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 16, 0, 0),
                                                child: Material(
                                                  color: Colors.transparent,
                                                  elevation: 0,
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                  ),
                                                  child: Container(
                                                    width: double.infinity,
                                                    height: 60,
                                                    decoration: BoxDecoration(
                                                      boxShadow: [
                                                        BoxShadow(
                                                          blurRadius: 2,
                                                          color:
                                                              Color(0x33000000),
                                                          offset: Offset(0, 2),
                                                        )
                                                      ],
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              8),
                                                      border: Border.all(
                                                        width: 1,
                                                      ),
                                                    ),
                                                    child: Padding(
                                                      padding:
                                                          EdgeInsetsDirectional
                                                              .fromSTEB(
                                                                  5, 0, 0, 0),
                                                      child: Column(
                                                        mainAxisSize:
                                                            MainAxisSize.max,
                                                        crossAxisAlignment:
                                                            CrossAxisAlignment
                                                                .start,
                                                        children: [
                                                          Text(
                                                            'Miss doses?',
                                                            style: FlutterFlowTheme
                                                                    .of(context)
                                                                .subtitle1
                                                                .override(
                                                                  fontFamily: FlutterFlowTheme.of(
                                                                          context)
                                                                      .subtitle1Family,
                                                                  fontSize: 15,
                                                                  useGoogleFonts: GoogleFonts
                                                                          .asMap()
                                                                      .containsKey(
                                                                          FlutterFlowTheme.of(context)
                                                                              .subtitle1Family),
                                                                ),
                                                          ),
                                                          Expanded(
                                                            child:
                                                                FlutterFlowDropDown<
                                                                    String>(
                                                              options: [
                                                                'Yes',
                                                                'No'
                                                              ],
                                                              onChanged: (val) =>
                                                                  setState(() =>
                                                                      _model.missedDosesValue =
                                                                          val),
                                                              width: double
                                                                  .infinity,
                                                              height: 50,
                                                              textStyle:
                                                                  FlutterFlowTheme.of(
                                                                          context)
                                                                      .bodyText1
                                                                      .override(
                                                                        fontFamily:
                                                                            FlutterFlowTheme.of(context).bodyText1Family,
                                                                        color: FlutterFlowTheme.of(context)
                                                                            .primaryText,
                                                                        useGoogleFonts:
                                                                            GoogleFonts.asMap().containsKey(FlutterFlowTheme.of(context).bodyText1Family),
                                                                      ),
                                                              elevation: 2,
                                                              borderColor: Colors
                                                                  .transparent,
                                                              borderWidth: 0,
                                                              borderRadius: 0,
                                                              margin:
                                                                  EdgeInsetsDirectional
                                                                      .fromSTEB(
                                                                          12,
                                                                          4,
                                                                          12,
                                                                          4),
                                                              hidesUnderline:
                                                                  true,
                                                            ),
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                  ),
                                                ),
                                              ),
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 16, 0, 0),
                                                child: Material(
                                                  color: Colors.transparent,
                                                  elevation: 0,
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                  ),
                                                  child: Container(
                                                    width: double.infinity,
                                                    height: 60,
                                                    decoration: BoxDecoration(
                                                      boxShadow: [
                                                        BoxShadow(
                                                          blurRadius: 2,
                                                          color:
                                                              Color(0x33000000),
                                                          offset: Offset(0, 2),
                                                        )
                                                      ],
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              8),
                                                      border: Border.all(
                                                        width: 1,
                                                      ),
                                                    ),
                                                    child: Padding(
                                                      padding:
                                                          EdgeInsetsDirectional
                                                              .fromSTEB(
                                                                  5, 0, 0, 0),
                                                      child: Column(
                                                        mainAxisSize:
                                                            MainAxisSize.max,
                                                        crossAxisAlignment:
                                                            CrossAxisAlignment
                                                                .start,
                                                        children: [
                                                          Text(
                                                            'Adverse issues?',
                                                            style: FlutterFlowTheme
                                                                    .of(context)
                                                                .subtitle1
                                                                .override(
                                                                  fontFamily: FlutterFlowTheme.of(
                                                                          context)
                                                                      .subtitle1Family,
                                                                  fontSize: 15,
                                                                  useGoogleFonts: GoogleFonts
                                                                          .asMap()
                                                                      .containsKey(
                                                                          FlutterFlowTheme.of(context)
                                                                              .subtitle1Family),
                                                                ),
                                                          ),
                                                          Expanded(
                                                            child:
                                                                FlutterFlowDropDown<
                                                                    String>(
                                                              options: [
                                                                'Yes',
                                                                'No'
                                                              ],
                                                              onChanged: (val) =>
                                                                  setState(() =>
                                                                      _model.adverseIssuesValue =
                                                                          val),
                                                              width: double
                                                                  .infinity,
                                                              height: 50,
                                                              textStyle:
                                                                  FlutterFlowTheme.of(
                                                                          context)
                                                                      .bodyText1
                                                                      .override(
                                                                        fontFamily:
                                                                            FlutterFlowTheme.of(context).bodyText1Family,
                                                                        color: FlutterFlowTheme.of(context)
                                                                            .primaryText,
                                                                        useGoogleFonts:
                                                                            GoogleFonts.asMap().containsKey(FlutterFlowTheme.of(context).bodyText1Family),
                                                                      ),
                                                              elevation: 2,
                                                              borderColor: Colors
                                                                  .transparent,
                                                              borderWidth: 0,
                                                              borderRadius: 0,
                                                              margin:
                                                                  EdgeInsetsDirectional
                                                                      .fromSTEB(
                                                                          12,
                                                                          4,
                                                                          12,
                                                                          4),
                                                              hidesUnderline:
                                                                  true,
                                                            ),
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                  ),
                                                ),
                                              ),
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 16, 0, 0),
                                                child: Material(
                                                  color: Colors.transparent,
                                                  elevation: 0,
                                                  shape: RoundedRectangleBorder(
                                                    borderRadius:
                                                        BorderRadius.circular(
                                                            8),
                                                  ),
                                                  child: Container(
                                                    width:
                                                        MediaQuery.of(context)
                                                                .size
                                                                .width *
                                                            0.9,
                                                    height: 60,
                                                    decoration: BoxDecoration(
                                                      boxShadow: [
                                                        BoxShadow(
                                                          blurRadius: 2,
                                                          color:
                                                              Color(0x33000000),
                                                          offset: Offset(0, 2),
                                                        )
                                                      ],
                                                      borderRadius:
                                                          BorderRadius.circular(
                                                              8),
                                                      border: Border.all(
                                                        width: 1,
                                                      ),
                                                    ),
                                                    child: Row(
                                                      mainAxisSize:
                                                          MainAxisSize.max,
                                                      children: [
                                                        Expanded(
                                                          child: Column(
                                                            mainAxisSize:
                                                                MainAxisSize
                                                                    .max,
                                                            mainAxisAlignment:
                                                                MainAxisAlignment
                                                                    .center,
                                                            crossAxisAlignment:
                                                                CrossAxisAlignment
                                                                    .start,
                                                            children: [
                                                              Padding(
                                                                padding:
                                                                    EdgeInsetsDirectional
                                                                        .fromSTEB(
                                                                            20,
                                                                            0,
                                                                            0,
                                                                            0),
                                                                child: Text(
                                                                  'Date Next Refill',
                                                                  style: FlutterFlowTheme.of(
                                                                          context)
                                                                      .bodyText1
                                                                      .override(
                                                                        fontFamily:
                                                                            FlutterFlowTheme.of(context).bodyText1Family,
                                                                        color: FlutterFlowTheme.of(context)
                                                                            .primaryText,
                                                                        fontSize:
                                                                            12,
                                                                        useGoogleFonts:
                                                                            GoogleFonts.asMap().containsKey(FlutterFlowTheme.of(context).bodyText1Family),
                                                                      ),
                                                                ),
                                                              ),
                                                              Padding(
                                                                padding:
                                                                    EdgeInsetsDirectional
                                                                        .fromSTEB(
                                                                            20,
                                                                            4,
                                                                            0,
                                                                            0),
                                                                child: Text(
                                                                  dateTimeFormat(
                                                                      'yMMMd',
                                                                      _model
                                                                          .datePicked),
                                                                  style: FlutterFlowTheme.of(
                                                                          context)
                                                                      .bodyText2
                                                                      .override(
                                                                        fontFamily:
                                                                            FlutterFlowTheme.of(context).bodyText2Family,
                                                                        color: FlutterFlowTheme.of(context)
                                                                            .primaryText,
                                                                        fontSize:
                                                                            12,
                                                                        fontWeight:
                                                                            FontWeight.w600,
                                                                        useGoogleFonts:
                                                                            GoogleFonts.asMap().containsKey(FlutterFlowTheme.of(context).bodyText2Family),
                                                                      ),
                                                                ),
                                                              ),
                                                            ],
                                                          ),
                                                        ),
                                                        Padding(
                                                          padding:
                                                              EdgeInsetsDirectional
                                                                  .fromSTEB(0,
                                                                      0, 8, 0),
                                                          child: Column(
                                                            mainAxisSize:
                                                                MainAxisSize
                                                                    .max,
                                                            mainAxisAlignment:
                                                                MainAxisAlignment
                                                                    .center,
                                                            children: [
                                                              FlutterFlowIconButton(
                                                                borderColor: Colors
                                                                    .transparent,
                                                                borderRadius:
                                                                    30,
                                                                buttonSize: 46,
                                                                icon: Icon(
                                                                  Icons
                                                                      .date_range_outlined,
                                                                  color: FlutterFlowTheme.of(
                                                                          context)
                                                                      .primaryColor,
                                                                  size: 20,
                                                                ),
                                                                onPressed:
                                                                    () async {
                                                                  DateTime nrd = DateTime(
                                                                      getCurrentTimestamp
                                                                          .year,
                                                                      getCurrentTimestamp
                                                                          .month,
                                                                      getCurrentTimestamp
                                                                              .day +
                                                                          (int.tryParse(_model.qtyDispensedController.text) ??
                                                                              0));
                                                                  final _datePickedDate =
                                                                      await showDatePicker(
                                                                    context:
                                                                        context,
                                                                    initialDate:
                                                                        nrd,
                                                                    firstDate:
                                                                        getCurrentTimestamp,
                                                                    lastDate:
                                                                        DateTime(
                                                                            2050),
                                                                  );

                                                                  if (_datePickedDate !=
                                                                      null) {
                                                                    setState(
                                                                        () {
                                                                      _model.datePicked =
                                                                          DateTime(
                                                                        _datePickedDate
                                                                            .year,
                                                                        _datePickedDate
                                                                            .month,
                                                                        _datePickedDate
                                                                            .day,
                                                                      );
                                                                    });
                                                                  }
                                                                },
                                                              ),
                                                            ],
                                                          ),
                                                        ),
                                                      ],
                                                    ),
                                                  ),
                                                ),
                                              ),
                                              Padding(
                                                padding: EdgeInsetsDirectional
                                                    .fromSTEB(0, 30, 0, 0),
                                                child: Row(
                                                  mainAxisSize:
                                                      MainAxisSize.max,
                                                  mainAxisAlignment:
                                                      MainAxisAlignment
                                                          .spaceBetween,
                                                  children: [
                                                    FFButtonWidget(
                                                      onPressed: () async {
                                                        context.pop();
                                                      },
                                                      text: 'Cancel',
                                                      options: FFButtonOptions(
                                                        width: 130,
                                                        height: 40,
                                                        padding:
                                                            EdgeInsetsDirectional
                                                                .fromSTEB(
                                                                    0, 0, 0, 0),
                                                        iconPadding:
                                                            EdgeInsetsDirectional
                                                                .fromSTEB(
                                                                    0, 0, 0, 0),
                                                        color:
                                                            FlutterFlowTheme.of(
                                                                    context)
                                                                .secondaryColor,
                                                        textStyle:
                                                            FlutterFlowTheme.of(
                                                                    context)
                                                                .subtitle2
                                                                .override(
                                                                  fontFamily: FlutterFlowTheme.of(
                                                                          context)
                                                                      .subtitle2Family,
                                                                  color: Colors
                                                                      .white,
                                                                  useGoogleFonts: GoogleFonts
                                                                          .asMap()
                                                                      .containsKey(
                                                                          FlutterFlowTheme.of(context)
                                                                              .subtitle2Family),
                                                                ),
                                                        borderSide: BorderSide(
                                                          color: Colors
                                                              .transparent,
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            BorderRadius
                                                                .circular(8),
                                                      ),
                                                    ),
                                                    FFButtonWidget(
                                                      onPressed: (int.tryParse(_model
                                                                          .qtyDispensedController
                                                                          .text) !=
                                                                      null &&
                                                                  int.parse(_model
                                                                          .qtyDispensedController
                                                                          .text) >
                                                                      FFAppState()
                                                                          .regimenQty ||
                                                              (nextVisitIsAfterNextVLAppointment() ||
                                                                  nextVisitIsAfterNextTPTAppointment() ||
                                                                  nextVisitIsAfterNextCancerScreeningAppointment()))
                                                          ? null
                                                          : () async {
                                                              if (_model.formKey
                                                                          .currentState ==
                                                                      null ||
                                                                  !_model
                                                                      .formKey
                                                                      .currentState!
                                                                      .validate()) {
                                                                return;
                                                              }
                                                              var clinic = ClinicData(
                                                                  null,
                                                                  int.tryParse(_model
                                                                      .systolicController
                                                                      .text),
                                                                  int.tryParse(_model
                                                                      .diastolicController
                                                                      .text),
                                                                  double.tryParse(_model
                                                                      .weightController
                                                                      .text),
                                                                  double.tryParse(_model
                                                                      .temperatureController
                                                                      .text),
                                                                  _model.patient?.id ??
                                                                      0,
                                                                  getCurrentTimestamp,
                                                                  functions.booleanFromYesNo(_model
                                                                      .caughingValue),
                                                                  functions.booleanFromYesNo(
                                                                      _model
                                                                          .swellingsValue),
                                                                  functions.booleanFromYesNo(
                                                                      _model.nightSweatValue),
                                                                  functions.booleanFromYesNo(_model.feverValue),
                                                                  functions.booleanFromYesNo(_model.weightLossValue),
                                                                  functions.booleanFromYesNo(_model.tbReferValue),
                                                                  false);
                                                              database.then(
                                                                  (value) => value
                                                                      .clinicDao
                                                                      .insertRecord(
                                                                          clinic));
                                                              var refill = Refill(
                                                                  null,
                                                                  getCurrentTimestamp,
                                                                  _model.patient
                                                                          ?.assignedRegimen ??
                                                                      '',
                                                                  _model.patient
                                                                          ?.id ??
                                                                      0,
                                                                  int.tryParse(_model.qtyPrescribedController.text) ??
                                                                      0,
                                                                  int.tryParse(
                                                                          _model
                                                                              .qtyDispensedController
                                                                              .text) ??
                                                                      0,
                                                                  _model
                                                                      .datePicked!,
                                                                  functions
                                                                      .booleanFromYesNo(
                                                                          _model
                                                                              .missedDosesValue),
                                                                  functions
                                                                      .booleanFromYesNo(
                                                                          _model
                                                                              .adverseIssuesValue),
                                                                  _model
                                                                      .barcode,
                                                                  false);
                                                              database.then(
                                                                  (value) => value
                                                                      .refillDao
                                                                      .insertRecord(
                                                                          refill));
                                                              var quantity =
                                                                  double.tryParse(_model
                                                                          .qtyDispensedController
                                                                          .text) ??
                                                                      0;

                                                              while (quantity >
                                                                  0) {
                                                                var inventory =
                                                                    await getNonZeroInventory(
                                                                        _model.patient?.assignedRegimen ??
                                                                            '');
                                                                if (inventory !=
                                                                    null) {
                                                                  if (inventory
                                                                          .quantity >=
                                                                      quantity) {
                                                                    var balance =
                                                                        inventory.quantity -
                                                                            quantity;
                                                                    database.then((value) => value
                                                                        .inventoryDao
                                                                        .updateQuantity(
                                                                            inventory.id!,
                                                                            balance.toInt()));
                                                                    quantity =
                                                                        0;
                                                                  } else {
                                                                    database.then((value) => value
                                                                        .inventoryDao
                                                                        .updateQuantity(
                                                                            inventory.id!,
                                                                            0));
                                                                    quantity -=
                                                                        inventory
                                                                            .quantity;
                                                                  }
                                                                }
                                                              }
                                                              context.pop();
                                                            },
                                                      text: 'Save',
                                                      options: FFButtonOptions(
                                                        width: 130,
                                                        height: 40,
                                                        padding:
                                                            EdgeInsetsDirectional
                                                                .fromSTEB(
                                                                    0, 0, 0, 0),
                                                        iconPadding:
                                                            EdgeInsetsDirectional
                                                                .fromSTEB(
                                                                    0, 0, 0, 0),
                                                        color:
                                                            FlutterFlowTheme.of(
                                                                    context)
                                                                .primaryColor,
                                                        textStyle:
                                                            FlutterFlowTheme.of(
                                                                    context)
                                                                .subtitle2
                                                                .override(
                                                                  fontFamily: FlutterFlowTheme.of(
                                                                          context)
                                                                      .subtitle2Family,
                                                                  color: Colors
                                                                      .white,
                                                                  useGoogleFonts: GoogleFonts
                                                                          .asMap()
                                                                      .containsKey(
                                                                          FlutterFlowTheme.of(context)
                                                                              .subtitle2Family),
                                                                ),
                                                        borderSide: BorderSide(
                                                          color: Colors
                                                              .transparent,
                                                          width: 1,
                                                        ),
                                                        borderRadius:
                                                            BorderRadius
                                                                .circular(8),
                                                      ),
                                                    ),
                                                  ],
                                                ),
                                              ),
                                            ],
                                          ),
                                        ),
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                            Align(
                              alignment: AlignmentDirectional(0, -0.95),
                              child: Padding(
                                padding:
                                    EdgeInsetsDirectional.fromSTEB(0, 0, 0, 10),
                                child:
                                    smooth_page_indicator.SmoothPageIndicator(
                                  controller: _model.pageViewController ??=
                                      PageController(initialPage: 0),
                                  count: 3,
                                  axisDirection: Axis.horizontal,
                                  onDotClicked: (i) {
                                    _model.pageViewController!.animateToPage(
                                      i,
                                      duration: Duration(milliseconds: 500),
                                      curve: Curves.ease,
                                    );
                                  },
                                  effect:
                                      smooth_page_indicator.ExpandingDotsEffect(
                                    expansionFactor: 2,
                                    spacing: 8,
                                    radius: 16,
                                    dotWidth: 16,
                                    dotHeight: 16,
                                    dotColor: FlutterFlowTheme.of(context)
                                        .tertiaryColor,
                                    activeDotColor: FlutterFlowTheme.of(context)
                                        .secondaryColor,
                                    paintStyle: PaintingStyle.fill,
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
