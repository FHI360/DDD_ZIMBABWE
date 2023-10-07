import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:impilo/backend/floor/entities/inventory.dart';
import 'package:impilo/backend/floor/entities/inventory_request.dart';
import 'package:impilo/backend/http/Inventory_service.dart';
import 'package:impilo/main.dart';
import 'package:provider/provider.dart';
import 'package:uuid/uuid.dart';

import '/components/inventory_request_widget.dart';
import '/custom_code/actions/index.dart' as actions;
import '/flutter_flow/flutter_flow_icon_button.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import 'inventory_request_list_model.dart';

export 'inventory_request_list_model.dart';

class InventoryRequestListWidget extends StatefulWidget {
  const InventoryRequestListWidget({Key? key}) : super(key: key);

  @override
  _InventoryRequestListWidgetState createState() =>
      _InventoryRequestListWidgetState();
}

class _InventoryRequestListWidgetState
    extends State<InventoryRequestListWidget> {
  late InventoryRequestListModel _model;

  final scaffoldKey = GlobalKey<ScaffoldState>();
  final _unfocusNode = FocusNode();

  Future<List<InventoryRequest>> getInventoryRequest() async {
    var _database = await database;
    return _database.inventoryRequestDao.findAll(FFAppState().code);
  }

  @override
  void initState() {
    super.initState();
    _model = createModel(context, () => InventoryRequestListModel());

    // On page load action.
    SchedulerBinding.instance.addPostFrameCallback((_) async {
      await actions.updateRegimenList();
    });

    WidgetsBinding.instance.addPostFrameCallback((_) => setState(() {}));
  }

  synchronize() async {
    var inventory = await InventoryService().fulfill(FFAppState().code);
    var _database = await database;
    if (inventory != null) {
      for (var i = 0; i < inventory.length; i++) {
        var curr = await _database.inventoryDao.findByUniqueIdAndRegimen(
            inventory[i]['requestReference'], inventory[i]['regimen']);
        var exists = curr.where((element) => element.reference ==  inventory[i]['reference']).isNotEmpty;
        if (!exists) {
          var _inventory = Inventory(
              null,
              inventory[i]['requestReference'],
              inventory[i]['reference'],
              inventory[i]['regimen'],
              inventory[i]['bottles'],
              inventory[i]['balance'],
              inventory[i]['acknowledged'] ?? false,
              inventory[i]['batchNo'],
              inventory[i]['barcode'],
              FFAppState().code,
              DateTime.parse(inventory[i]['expirationDate']),
              inventory[i]['batchIssueId'] ?? '');
          await _database.inventoryDao.insertRecord(_inventory);
          await _database.inventoryRequestDao
              .fulfilled(inventory[i]['bottles'], inventory[i]['requestReference']);
        }
      }
      setState(() {});
    }
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
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          await showModalBottomSheet(
            isScrollControlled: true,
            backgroundColor: Colors.transparent,
            barrierColor: Color(0x00000000),
            enableDrag: false,
            context: context,
            builder: (context) {
              return Padding(
                padding: MediaQuery.of(context).viewInsets,
                child: InventoryRequestWidget(),
              );
            },
          ).then((value) => setState(() {}));
        },
        backgroundColor: FlutterFlowTheme.of(context).primaryColor,
        elevation: 8,
        child: FlutterFlowIconButton(
          borderColor: Colors.transparent,
          borderRadius: 30,
          borderWidth: 1,
          buttonSize: 60,
          icon: Icon(
            Icons.add,
            color: Colors.white,
            size: 30,
          ),
          onPressed: () async {
            await showModalBottomSheet(
              isScrollControlled: true,
              backgroundColor: Colors.transparent,
              barrierColor: Color(0x00000000),
              enableDrag: false,
              context: context,
              builder: (context) {
                return Padding(
                  padding: MediaQuery.of(context).viewInsets,
                  child: InventoryRequestWidget(),
                );
              },
            ).then((value) => setState(() {}));
          },
        ),
      ),
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
            context.pushNamed('inventoryHome');
          },
        ),
        title: Text(
          'Inventory Request',
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
      body: GestureDetector(
        onTap: () => FocusScope.of(context).requestFocus(_unfocusNode),
        child: Container(
          width: double.infinity,
          height: double.infinity,
          color: Colors.white,
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.max,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                InkWell(
                  onTap: () async {},
                  child: Column(
                    mainAxisSize: MainAxisSize.max,
                    mainAxisAlignment:
                    MainAxisAlignment.center,
                    children: [
                      FlutterFlowIconButton(
                        borderColor: Colors.transparent,
                        borderRadius: 30,
                        borderWidth: 1,
                        buttonSize: 60,
                        icon: Icon(
                          Icons.refresh_rounded,
                          color:
                          FlutterFlowTheme.of(context)
                              .primaryText,
                          size: 30,
                        ),
                        onPressed: () async {
                          await synchronize();
                          setState(() {
                            _model.fullList = true;
                          });
                        },
                      ),
                    ],
                  ),
                ),
                if (_model.fullList)
                  Padding(
                    padding: EdgeInsetsDirectional.fromSTEB(0, 12, 0, 44),
                    child: FutureBuilder<List<InventoryRequest>>(
                      future: getInventoryRequest(),
                      builder: (context, snapshot) {
                        // Customize what your widget looks like when it's loading.
                        if (!snapshot.hasData) {
                          return Center(
                            child: SizedBox(
                              width: 50,
                              height: 50,
                              child: SpinKitCircle(
                                color:
                                    FlutterFlowTheme.of(context).primaryColor,
                                size: 50,
                              ),
                            ),
                          );
                        }
                        List<InventoryRequest> listViewInventoryRequestRowList =
                            snapshot.data!;
                        return ListView.builder(
                          padding: EdgeInsets.zero,
                          primary: false,
                          shrinkWrap: true,
                          scrollDirection: Axis.vertical,
                          itemCount: listViewInventoryRequestRowList.length,
                          itemBuilder: (context, listViewIndex) {
                            final listViewInventoryRequestRow =
                                listViewInventoryRequestRowList[listViewIndex];
                            return Padding(
                              padding:
                                  EdgeInsetsDirectional.fromSTEB(16, 4, 16, 8),
                              child: Container(
                                width: double.infinity,
                                height: 150,
                                decoration: BoxDecoration(
                                  color: listViewInventoryRequestRow.quantity ==
                                          listViewInventoryRequestRow
                                              .quantityFulfilled
                                      ? Colors.green
                                      : Colors.amber,
                                  boxShadow: [
                                    BoxShadow(
                                      blurRadius: 4,
                                      color: Color(0x32000000),
                                      offset: Offset(0, 2),
                                    )
                                  ],
                                  borderRadius: BorderRadius.circular(8),
                                  shape: BoxShape.rectangle,
                                ),
                                child: Padding(
                                  padding: EdgeInsetsDirectional.fromSTEB(
                                      8, 0, 8, 0),
                                  child: Row(
                                    mainAxisSize: MainAxisSize.max,
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                    children: [
                                      Expanded(
                                        child: Padding(
                                          padding:
                                              EdgeInsetsDirectional.fromSTEB(
                                                  12, 0, 0, 0),
                                          child: Column(
                                            mainAxisSize: MainAxisSize.max,
                                            mainAxisAlignment:
                                                MainAxisAlignment.spaceEvenly,
                                            crossAxisAlignment:
                                                CrossAxisAlignment.start,
                                            children: [
                                              Text(
                                                listViewInventoryRequestRow
                                                    .regimen,
                                                style:
                                                    FlutterFlowTheme.of(context)
                                                        .subtitle1,
                                              ),
                                              Row(
                                                mainAxisSize: MainAxisSize.max,
                                                children: [
                                                  Padding(
                                                    padding:
                                                        EdgeInsetsDirectional
                                                            .fromSTEB(
                                                                0, 0, 5, 0),
                                                    child: Text(
                                                      'Quantity',
                                                      style:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .bodyText1,
                                                    ),
                                                  ),
                                                  Text(
                                                    formatNumber(
                                                      listViewInventoryRequestRow
                                                          .quantity,
                                                      formatType:
                                                          FormatType.custom,
                                                      format: '#,##0',
                                                      locale: '',
                                                    ),
                                                    style: FlutterFlowTheme.of(
                                                            context)
                                                        .bodyText1,
                                                  ),
                                                ],
                                              ),
                                              Row(
                                                mainAxisSize: MainAxisSize.max,
                                                children: [
                                                  Padding(
                                                    padding:
                                                        EdgeInsetsDirectional
                                                            .fromSTEB(
                                                                0, 0, 5, 0),
                                                    child: Text(
                                                      'Quantity fulfilled',
                                                      style:
                                                          FlutterFlowTheme.of(
                                                                  context)
                                                              .bodyText1,
                                                    ),
                                                  ),
                                                  Text(
                                                    formatNumber(
                                                      listViewInventoryRequestRow
                                                          .quantityFulfilled,
                                                      formatType:
                                                          FormatType.custom,
                                                      format: '#,##0',
                                                      locale: '',
                                                    ),
                                                    style: FlutterFlowTheme.of(
                                                            context)
                                                        .bodyText1,
                                                  ),
                                                ],
                                              ),
                                              if (listViewInventoryRequestRow
                                                      .quantityFulfilled ==
                                                  listViewInventoryRequestRow
                                                      .quantity)
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
                                                      Icon(
                                                        Icons.check_circle,
                                                        color:
                                                            FlutterFlowTheme.of(
                                                                    context)
                                                                .primaryColor,
                                                        size: 24,
                                                      ),
                                                      Text(
                                                        'Fulfilled',
                                                        style:
                                                            FlutterFlowTheme.of(
                                                                    context)
                                                                .bodyText1,
                                                      ),
                                                    ],
                                                  ),
                                                ),
                                            ],
                                          ),
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            );
                          },
                        );
                      },
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
