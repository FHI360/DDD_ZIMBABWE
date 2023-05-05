import 'dart:convert';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:printing/printing.dart';

class PdfPreviewPage extends StatelessWidget {
  final String data;

  const PdfPreviewPage({Key? key, required this.data}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('PDF Preview'),
      ),
      body: PdfPreview(
        build: (context) => base64Decode(data),
      ),
    );
  }
}
