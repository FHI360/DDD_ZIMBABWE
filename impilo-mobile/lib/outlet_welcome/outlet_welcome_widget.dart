import '/flutter_flow/flutter_flow_icon_button.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import 'package:flutter/material.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';
import 'outlet_welcome_model.dart';
export 'outlet_welcome_model.dart';

class OutletWelcomeWidget extends StatefulWidget {
  const OutletWelcomeWidget({Key? key}) : super(key: key);

  @override
  _OutletWelcomeWidgetState createState() => _OutletWelcomeWidgetState();
}

class _OutletWelcomeWidgetState extends State<OutletWelcomeWidget> {
  late OutletWelcomeModel _model;

  final scaffoldKey = GlobalKey<ScaffoldState>();
  final _unfocusNode = FocusNode();

  @override
  void initState() {
    super.initState();
    _model = createModel(context, () => OutletWelcomeModel());

    WidgetsBinding.instance.addPostFrameCallback((_) => setState(() {}));
  }

  @override
  void dispose() {
    _model.dispose();

    _unfocusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    context.watch<FFAppState>();

    return Scaffold(
      key: scaffoldKey,
      backgroundColor: FlutterFlowTheme.of(context).primaryBackground,
      body: SafeArea(
        child: GestureDetector(
          onTap: () => FocusScope.of(context).requestFocus(_unfocusNode),
          child: Container(
            width: double.infinity,
            height: double.infinity,
            decoration: BoxDecoration(
              color: FlutterFlowTheme.of(context).secondaryBackground,
              image: DecorationImage(
                fit: BoxFit.cover,
                image: Image.asset(
                  'assets/images/outhome2.png',
                ).image,
              ),
            ),
            child: Column(
              mainAxisSize: MainAxisSize.max,
              children: [
                Padding(
                  padding:
                      EdgeInsetsDirectional.fromSTEB(10.0, 400.0, 10.0, 0.0),
                  child: Row(
                    mainAxisSize: MainAxisSize.max,
                    children: [
                      Container(
                        width: 262.4,
                        height: 100.0,
                        decoration: BoxDecoration(),
                        child: Wrap(
                          spacing: 0.0,
                          runSpacing: 0.0,
                          alignment: WrapAlignment.start,
                          crossAxisAlignment: WrapCrossAlignment.start,
                          direction: Axis.horizontal,
                          runAlignment: WrapAlignment.start,
                          verticalDirection: VerticalDirection.down,
                          clipBehavior: Clip.none,
                          children: [
                            Text(
                              'Welcome to Impilo Refill App ',
                              style: FlutterFlowTheme.of(context).title1,
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
                Row(
                  mainAxisSize: MainAxisSize.max,
                  children: [],
                ),
                Padding(
                  padding:
                      EdgeInsetsDirectional.fromSTEB(0.0, 150.0, 20.0, 0.0),
                  child: Row(
                    mainAxisSize: MainAxisSize.max,
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      FlutterFlowIconButton(
                        borderColor:
                            FlutterFlowTheme.of(context).primaryBtnText,
                        borderRadius: 3.0,
                        borderWidth: 1.0,
                        buttonSize: 60.0,
                        fillColor:
                            FlutterFlowTheme.of(context).primaryBackground,
                        icon: Icon(
                          Icons.keyboard_arrow_right_outlined,
                          color: FlutterFlowTheme.of(context).tertiaryColor,
                          size: 30.0,
                        ),
                        onPressed: () {
                          print('IconButton pressed ...');
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
    );
  }
}
