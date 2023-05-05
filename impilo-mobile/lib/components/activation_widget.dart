import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

import '/backend/api_requests/api_calls.dart';
import '/flutter_flow/flutter_flow_icon_button.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import '/flutter_flow/flutter_flow_widgets.dart';
import '../custom_code/actions/save_records_from_activation.dart';
import 'activation_model.dart';

export 'activation_model.dart';

class ActivationWidget extends StatefulWidget {
  const ActivationWidget({Key? key}) : super(key: key);

  @override
  _ActivationWidgetState createState() => _ActivationWidgetState();
}

class _ActivationWidgetState extends State<ActivationWidget> {
  late ActivationModel _model;

  @override
  void setState(VoidCallback callback) {
    super.setState(callback);
    _model.onUpdate();
  }

  @override
  void initState() {
    super.initState();
    _model = createModel(context, () => ActivationModel());

    WidgetsBinding.instance.addPostFrameCallback((_) => setState(() {}));
  }

  @override
  void dispose() {
    _model.maybeDispose();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    context.watch<FFAppState>();

    return Align(
      alignment: AlignmentDirectional(0.0, 0.0),
      child: Padding(
        padding: EdgeInsetsDirectional.fromSTEB(1.0, 1.0, 1.0, 1.0),
        child: Container(
          width: double.infinity,
          constraints: BoxConstraints(
            maxWidth: 570.0,
          ),
          decoration: BoxDecoration(
            color: FlutterFlowTheme.of(context).secondaryText,
            borderRadius: BorderRadius.circular(12.0),
            border: Border.all(
              color: FlutterFlowTheme.of(context).lineColor,
            ),
          ),
          child: Padding(
            padding: EdgeInsetsDirectional.fromSTEB(24.0, 24.0, 24.0, 24.0),
            child: Column(
              mainAxisSize: MainAxisSize.max,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisSize: MainAxisSize.max,
                  children: [
                    Expanded(
                      child: Padding(
                        padding:
                            EdgeInsetsDirectional.fromSTEB(0.0, 0.0, 12.0, 0.0),
                        child: Text(
                          'Site Synchronization',
                          style: FlutterFlowTheme.of(context).title2.override(
                                fontFamily: 'Roboto',
                                useGoogleFonts: GoogleFonts.asMap().containsKey(
                                    FlutterFlowTheme.of(context).title2Family),
                              ),
                        ),
                      ),
                    ),
                    FlutterFlowIconButton(
                      borderRadius: 30.0,
                      borderWidth: 2.0,
                      buttonSize: 44.0,
                      fillColor: Color(0xFFB7463A),
                      icon: Icon(
                        Icons.close_rounded,
                        color: FlutterFlowTheme.of(context).secondaryText,
                        size: 24.0,
                      ),
                      onPressed: () async {
                        Navigator.pop(context);
                      },
                    ),
                  ],
                ),
                Divider(
                  height: 24.0,
                  thickness: 2.0,
                  color: FlutterFlowTheme.of(context).primaryBackground,
                ),
                Padding(
                  padding: EdgeInsetsDirectional.fromSTEB(0.0, 16.0, 0.0, 0.0),
                  child: Row(
                    mainAxisSize: MainAxisSize.max,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      FFButtonWidget(
                        onPressed: () async {
                          Navigator.pop(context);
                        },
                        text: 'Cancel',
                        options: FFButtonOptions(
                          height: 40.0,
                          padding: EdgeInsetsDirectional.fromSTEB(
                              24.0, 0.0, 24.0, 0.0),
                          iconPadding: EdgeInsetsDirectional.fromSTEB(
                              0.0, 0.0, 0.0, 0.0),
                          color: FlutterFlowTheme.of(context).tertiaryColor,
                          textStyle: FlutterFlowTheme.of(context)
                              .subtitle1
                              .override(
                                fontFamily: 'Roboto',
                                useGoogleFonts: GoogleFonts.asMap().containsKey(
                                    FlutterFlowTheme.of(context)
                                        .subtitle1Family),
                              ),
                          elevation: 0.0,
                          borderSide: BorderSide(
                            width: 2.0,
                          ),
                          borderRadius: BorderRadius.circular(8.0),
                          hoverColor:
                              FlutterFlowTheme.of(context).primaryBackground,
                          hoverBorderSide: BorderSide(
                            width: 2.0,
                          ),
                          hoverTextColor:
                              FlutterFlowTheme.of(context).primaryText,
                        ),
                      ),
                      FFButtonWidget(
                        onPressed: () async {
                          var _shouldSetState = false;
                          _model.patients = await SiteActivationCall.call(
                            code: FFAppState().code,
                          );
                          _shouldSetState = true;
                          if (!(_model.patients?.succeeded ?? true) ||
                              _model.patients?.jsonBody['site'] == null) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(
                                content: Text(
                                  'Error synchronizing with facility',
                                  style: TextStyle(
                                    color: FlutterFlowTheme.of(context)
                                        .primaryText,
                                  ),
                                ),
                                duration: Duration(milliseconds: 4000),
                                backgroundColor: Color(0xFFB7463A),
                              ),
                            );
                            if (_shouldSetState) setState(() {});
                            return;
                          } else {
                            FFAppState().name =
                                _model.patients?.jsonBody['site'];
                            //Loop and create records
                            List<dynamic> patients =
                                _model.patients?.jsonBody['patients'];
                            saveRecordsFromActivation(
                                patients, FFAppState().code);
                          }

                          context.pushNamed('patientList');

                          if (_shouldSetState) setState(() {});
                        },
                        text: 'Synchronization',
                        options: FFButtonOptions(
                          width: 130.0,
                          height: 40.0,
                          padding: EdgeInsetsDirectional.fromSTEB(
                              0.0, 0.0, 0.0, 0.0),
                          iconPadding: EdgeInsetsDirectional.fromSTEB(
                              0.0, 0.0, 0.0, 0.0),
                          color: FlutterFlowTheme.of(context).primaryColor,
                          textStyle: FlutterFlowTheme.of(context)
                              .subtitle2
                              .override(
                                fontFamily: 'Roboto',
                                color: Colors.white,
                                useGoogleFonts: GoogleFonts.asMap().containsKey(
                                    FlutterFlowTheme.of(context)
                                        .subtitle2Family),
                              ),
                          elevation: 3.0,
                          borderSide: BorderSide(
                            color: Colors.transparent,
                            width: 1.0,
                          ),
                          borderRadius: BorderRadius.circular(8.0),
                          hoverColor: Color(0xFF2B16ED),
                          hoverBorderSide: BorderSide(
                            width: 1.0,
                          ),
                          hoverTextColor:
                              FlutterFlowTheme.of(context).primaryBtnText,
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
    );
  }
}
