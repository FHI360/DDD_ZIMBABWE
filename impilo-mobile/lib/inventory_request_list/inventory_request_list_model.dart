import 'package:flutter/material.dart';

import '/flutter_flow/flutter_flow_util.dart';

class InventoryRequestListModel extends FlutterFlowModel {
  ///  Local state fields for this page.

  bool fullList = true;

  List<dynamic> filteredClients = [];
  void addToFilteredClients(dynamic item) => filteredClients.add(item);
  void removeFromFilteredClients(dynamic item) => filteredClients.remove(item);
  void removeAtIndexFromFilteredClients(int index) =>
      filteredClients.removeAt(index);

  ///  State fields for stateful widgets in this page.

  /// Initialization and disposal methods.

  void initState(BuildContext context) {}

  void dispose() {}

  /// Additional helper methods are added here.

}
