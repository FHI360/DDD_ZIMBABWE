String formatAge(DateTime? dob) {
  if (dob == null) {
    return '';
  }
  DateTime now = DateTime.now();
  Duration difference = now.difference(dob);
  double age = difference.inDays / 365.25;

  return '${age.toStringAsFixed(0)} Yrs';
}

String? formatDate(String? date) {
  return '';
}

bool? booleanFromYesNo(String? value) {
  if (value == null) {
    return null;
  }
  return value == 'Yes'
      ? true
      : value == 'No'
          ? false
          : null;
}
