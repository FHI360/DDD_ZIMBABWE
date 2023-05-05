import '/flutter_flow/flutter_flow_icon_button.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import '/flutter_flow/flutter_flow_widgets.dart';
import 'package:smooth_page_indicator/smooth_page_indicator.dart'
as smooth_page_indicator;
import 'package:flutter/material.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

import 'inventory_home_model.dart';
export 'inventory_home_model.dart';

class InventoryHomeWidget extends StatefulWidget {
  const InventoryHomeWidget({Key? key}) : super(key: key);

  @override
  _InventoryHomeWidgetState createState() => _InventoryHomeWidgetState();
}

class _InventoryHomeWidgetState extends State<InventoryHomeWidget> {
  late InventoryHomeModel _model;

  final scaffoldKey = GlobalKey<ScaffoldState>();
  final _unfocusNode = FocusNode();

  @override
  void initState() {
    super.initState();
    _model = createModel(context, () => InventoryHomeModel());

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
            context.pushNamed('siteHome');
          },
        ),
        title: Text(
          FFAppState().name,
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
                  padding: EdgeInsetsDirectional.fromSTEB(10, 30, 10, 0),
                  child: Row(
                    mainAxisSize: MainAxisSize.max,
                    children: [
                      Container(
                        width: 262.4,
                        height: 100,
                        decoration: BoxDecoration(),
                        child: Wrap(
                          spacing: 0,
                          runSpacing: 0,
                          alignment: WrapAlignment.start,
                          crossAxisAlignment: WrapCrossAlignment.start,
                          direction: Axis.horizontal,
                          runAlignment: WrapAlignment.start,
                          verticalDirection: VerticalDirection.down,
                          clipBehavior: Clip.none,
                          children: [],
                        ),
                      ),
                    ],
                  ),
                ),
                Padding(
                  padding: EdgeInsetsDirectional.fromSTEB(20, 80, 20, 0),
                  child: Row(
                    mainAxisSize: MainAxisSize.max,
                    children: [
                      Expanded(
                        child: Padding(
                          padding:
                          EdgeInsetsDirectional.fromSTEB(20, 20, 20, 20),
                          child: Container(
                            width: double.infinity,
                            height: 500,
                            child: Stack(
                              children: [
                                Padding(
                                  padding: EdgeInsetsDirectional.fromSTEB(
                                      0, 0, 0, 50),
                                  child: PageView(
                                    controller: _model.pageViewController ??=
                                        PageController(initialPage: 0),
                                    scrollDirection: Axis.horizontal,
                                    children: [
                                      Card(
                                        clipBehavior:
                                        Clip.antiAliasWithSaveLayer,
                                        color: FlutterFlowTheme.of(context)
                                            .alternate,
                                        child: Column(
                                          mainAxisSize: MainAxisSize.max,
                                          children: [
                                            Padding(
                                              padding: EdgeInsetsDirectional
                                                  .fromSTEB(0, 40, 0, 20),
                                              child: Row(
                                                mainAxisSize: MainAxisSize.max,
                                                mainAxisAlignment:
                                                MainAxisAlignment.center,
                                                children: [
                                                  Text(
                                                    'Available Inventory',
                                                    style: FlutterFlowTheme.of(
                                                        context)
                                                        .title2,
                                                  ),
                                                ],
                                              ),
                                            ),
                                            Padding(
                                              padding: EdgeInsetsDirectional
                                                  .fromSTEB(20, 20, 20, 20),
                                              child: Row(
                                                mainAxisSize: MainAxisSize.max,
                                                mainAxisAlignment:
                                                MainAxisAlignment.center,
                                                children: [
                                                  Image.asset(
                                                    'assets/images/firstvisit.png',
                                                    width: 100,
                                                    height: 100,
                                                    fit: BoxFit.cover,
                                                  ),
                                                ],
                                              ),
                                            ),
                                            Row(
                                              mainAxisSize: MainAxisSize.max,
                                              mainAxisAlignment:
                                              MainAxisAlignment.center,
                                              children: [
                                                Text(
                                                  'Inventory  of Regimen in Site',
                                                  style: FlutterFlowTheme.of(
                                                      context)
                                                      .bodyText1
                                                      .override(
                                                    fontFamily:
                                                    FlutterFlowTheme.of(
                                                        context)
                                                        .bodyText1Family,
                                                    fontSize: 14,
                                                    useGoogleFonts: GoogleFonts
                                                        .asMap()
                                                        .containsKey(
                                                        FlutterFlowTheme.of(
                                                            context)
                                                            .bodyText1Family),
                                                  ),
                                                ),
                                              ],
                                            ),
                                            Padding(
                                              padding: EdgeInsetsDirectional
                                                  .fromSTEB(0, 16, 0, 0),
                                              child: Row(
                                                mainAxisSize: MainAxisSize.max,
                                                mainAxisAlignment:
                                                MainAxisAlignment.center,
                                                children: [
                                                  FFButtonWidget(
                                                    onPressed: () async {
                                                      context.pushNamed(
                                                          'inventoryList');
                                                    },
                                                    text: 'More',
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
                                                        color:
                                                        Colors.transparent,
                                                        width: 1,
                                                      ),
                                                      borderRadius:
                                                      BorderRadius.circular(
                                                          8),
                                                    ),
                                                  ),
                                                ],
                                              ),
                                            ),
                                          ],
                                        ),
                                      ),
                                      Card(
                                        clipBehavior:
                                        Clip.antiAliasWithSaveLayer,
                                        color: FlutterFlowTheme.of(context)
                                            .alternate,
                                        child: Column(
                                          mainAxisSize: MainAxisSize.max,
                                          children: [
                                            Padding(
                                              padding: EdgeInsetsDirectional
                                                  .fromSTEB(0, 40, 0, 20),
                                              child: Row(
                                                mainAxisSize: MainAxisSize.max,
                                                mainAxisAlignment:
                                                MainAxisAlignment.center,
                                                children: [
                                                  Text(
                                                    'Requested Inventory',
                                                    style: FlutterFlowTheme.of(
                                                        context)
                                                        .title2,
                                                  ),
                                                ],
                                              ),
                                            ),
                                            Padding(
                                              padding: EdgeInsetsDirectional
                                                  .fromSTEB(20, 20, 20, 20),
                                              child: Row(
                                                mainAxisSize: MainAxisSize.max,
                                                mainAxisAlignment:
                                                MainAxisAlignment.center,
                                                children: [
                                                  Image.asset(
                                                    'assets/images/inventorymgticon.png',
                                                    width: 100,
                                                    height: 100,
                                                    fit: BoxFit.cover,
                                                  ),
                                                ],
                                              ),
                                            ),
                                            Row(
                                              mainAxisSize: MainAxisSize.max,
                                              mainAxisAlignment:
                                              MainAxisAlignment.center,
                                              children: [
                                                Text(
                                                  'Manage drugs request from the facility',
                                                  style: FlutterFlowTheme.of(
                                                      context)
                                                      .bodyText1
                                                      .override(
                                                    fontFamily:
                                                    FlutterFlowTheme.of(
                                                        context)
                                                        .bodyText1Family,
                                                    fontSize: 14,
                                                    useGoogleFonts: GoogleFonts
                                                        .asMap()
                                                        .containsKey(
                                                        FlutterFlowTheme.of(
                                                            context)
                                                            .bodyText1Family),
                                                  ),
                                                ),
                                              ],
                                            ),
                                            Padding(
                                              padding: EdgeInsetsDirectional
                                                  .fromSTEB(0, 16, 0, 0),
                                              child: Row(
                                                mainAxisSize: MainAxisSize.max,
                                                mainAxisAlignment:
                                                MainAxisAlignment.center,
                                                children: [
                                                  FFButtonWidget(
                                                    onPressed: () async {
                                                      context.pushNamed(
                                                          'inventoryRequestList');
                                                    },
                                                    text: 'More',
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
                                                        color:
                                                        Colors.transparent,
                                                        width: 1,
                                                      ),
                                                      borderRadius:
                                                      BorderRadius.circular(
                                                          8),
                                                    ),
                                                  ),
                                                ],
                                              ),
                                            ),
                                          ],
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                                Align(
                                  alignment: AlignmentDirectional(0, 1),
                                  child: Padding(
                                    padding: EdgeInsetsDirectional.fromSTEB(
                                        0, 0, 0, 10),
                                    child: smooth_page_indicator
                                        .SmoothPageIndicator(
                                      controller: _model.pageViewController ??=
                                          PageController(initialPage: 0),
                                      count: 2,
                                      axisDirection: Axis.horizontal,
                                      onDotClicked: (i) {
                                        _model.pageViewController!
                                            .animateToPage(
                                          i,
                                          duration: Duration(milliseconds: 500),
                                          curve: Curves.ease,
                                        );
                                      },
                                      effect: smooth_page_indicator
                                          .ExpandingDotsEffect(
                                        expansionFactor: 2,
                                        spacing: 8,
                                        radius: 16,
                                        dotWidth: 16,
                                        dotHeight: 16,
                                        dotColor: Color(0xFF747550),
                                        activeDotColor:
                                        FlutterFlowTheme.of(context)
                                            .primaryColor,
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
