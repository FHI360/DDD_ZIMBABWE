import '/flutter_flow/flutter_flow_icon_button.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import 'package:flutter/material.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

class InventoryListModel extends FlutterFlowModel {
  ///  Local state fields for this page.

  bool fullList = true;

  List<dynamic> filteredClients = [];
  void addToFilteredClients(dynamic item) => filteredClients.add(item);
  void removeFromFilteredClients(dynamic item) => filteredClients.remove(item);
  void removeAtIndexFromFilteredClients(int index) =>
      filteredClients.removeAt(index);

  /// Initialization and disposal methods.

  void initState(BuildContext context) {}

  void dispose() {}

  /// Additional helper methods are added here.

}
