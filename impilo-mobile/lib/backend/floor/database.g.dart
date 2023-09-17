// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'database.dart';

// **************************************************************************
// FloorGenerator
// **************************************************************************

// ignore: avoid_classes_with_only_static_members
class $FloorAppDatabase {
  /// Creates a database builder for a persistent database.
  /// Once a database is built, you should keep a reference to it and re-use it.
  static _$AppDatabaseBuilder databaseBuilder(String name) =>
      _$AppDatabaseBuilder(name);

  /// Creates a database builder for an in memory database.
  /// Information stored in an in memory database disappears when the process is killed.
  /// Once a database is built, you should keep a reference to it and re-use it.
  static _$AppDatabaseBuilder inMemoryDatabaseBuilder() =>
      _$AppDatabaseBuilder(null);
}

class _$AppDatabaseBuilder {
  _$AppDatabaseBuilder(this.name);

  final String? name;

  final List<Migration> _migrations = [];

  Callback? _callback;

  /// Adds migrations to the builder.
  _$AppDatabaseBuilder addMigrations(List<Migration> migrations) {
    _migrations.addAll(migrations);
    return this;
  }

  /// Adds a database [Callback] to the builder.
  _$AppDatabaseBuilder addCallback(Callback callback) {
    _callback = callback;
    return this;
  }

  /// Creates the database and initializes it.
  Future<AppDatabase> build() async {
    final path = name != null
        ? await sqfliteDatabaseFactory.getDatabasePath(name!)
        : ':memory:';
    final database = _$AppDatabase();
    database.database = await database.open(
      path,
      _migrations,
      _callback,
    );
    return database;
  }
}

class _$AppDatabase extends AppDatabase {
  _$AppDatabase([StreamController<String>? listener]) {
    changeListener = listener ?? StreamController<String>.broadcast();
  }

  ClinicDao? _clinicDaoInstance;

  PatientDao? _patientDaoInstance;

  RefillDao? _refillDaoInstance;

  InventoryDao? _inventoryDaoInstance;

  InventoryRequestDao? _inventoryRequestDaoInstance;

  DevolveDao? _devolveDaoInstance;

  Future<sqflite.Database> open(
    String path,
    List<Migration> migrations, [
    Callback? callback,
  ]) async {
    final databaseOptions = sqflite.OpenDatabaseOptions(
      version: 2,
      onConfigure: (database) async {
        await database.execute('PRAGMA foreign_keys = ON');
        await callback?.onConfigure?.call(database);
      },
      onOpen: (database) async {
        await callback?.onOpen?.call(database);
      },
      onUpgrade: (database, startVersion, endVersion) async {
        await MigrationAdapter.runMigrations(
            database, startVersion, endVersion, migrations);

        await callback?.onUpgrade?.call(database, startVersion, endVersion);
      },
      onCreate: (database, version) async {
        await database.execute(
            'CREATE TABLE IF NOT EXISTS `clinic` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `systolic` INTEGER, `diastolic` INTEGER, `weight` REAL, `temperature` REAL, `patientId` TEXT NOT NULL, `date` INTEGER NOT NULL, `coughing` INTEGER, `swelling` INTEGER, `sweating` INTEGER, `fever` INTEGER, `weightLoss` INTEGER, `tbReferred` INTEGER, `synced` INTEGER NOT NULL)');
        await database.execute(
            'CREATE TABLE IF NOT EXISTS `Patient` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `givenName` TEXT NOT NULL, `familyName` TEXT NOT NULL, `hospitalNo` TEXT NOT NULL, `uniqueId` TEXT NOT NULL, `dateOfBirth` INTEGER NOT NULL, `sex` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, `assignedRegimen` TEXT NOT NULL, `facility` TEXT NOT NULL, `siteCode` TEXT NOT NULL, `address` TEXT NOT NULL, `lastClinicVisit` INTEGER NOT NULL, `lastRefillDate` INTEGER NOT NULL, `nextAppointmentDate` INTEGER NOT NULL, `nextRefillDate` INTEGER NOT NULL, `serviceDiscontinued` INTEGER NOT NULL, `reasonDiscontinued` TEXT NOT NULL, `dateDiscontinued` INTEGER NOT NULL, `uuid` TEXT NOT NULL, `synced` INTEGER NOT NULL)');
        await database.execute(
            'CREATE TABLE IF NOT EXISTS `Refill` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `date` INTEGER NOT NULL, `regimen` TEXT NOT NULL, `patientId` TEXT NOT NULL, `quantityPrescribed` INTEGER NOT NULL, `quantityDispensed` INTEGER NOT NULL, `dateNextRefill` INTEGER NOT NULL, `synced` INTEGER NOT NULL, `missedDoses` INTEGER, `adverseIssues` INTEGER, `barcode` TEXT, `batchIssuanceId` TEXT)');
        await database.execute(
            'CREATE TABLE IF NOT EXISTS `Inventory` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `uniqueId` TEXT NOT NULL, `reference` TEXT NOT NULL, `regimen` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `batchNo` TEXT NOT NULL, `barcode` TEXT NOT NULL, `siteCode` TEXT NOT NULL, `expiryDate` INTEGER NOT NULL, `batchIssuanceId` TEXT NOT NULL, `acknowledged` INTEGER NOT NULL, `synced` INTEGER NOT NULL)');
        await database.execute(
            'CREATE TABLE IF NOT EXISTS `InventoryRequest` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `uniqueId` TEXT NOT NULL, `regimen` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `quantityFulfilled` INTEGER NOT NULL, `siteCode` TEXT NOT NULL, `date` INTEGER NOT NULL, `synced` INTEGER NOT NULL)');
        await database.execute(
            'CREATE TABLE IF NOT EXISTS `Devolve` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `reasonDiscontinued` TEXT, `date` INTEGER NOT NULL, `outletCode` TEXT NOT NULL, `patientId` TEXT NOT NULL, `synced` INTEGER)');

        await database.execute(
            'CREATE VIEW IF NOT EXISTS `AssignedRegimen` AS select distinct assignedRegimen, siteCode from patient where serviceDiscontinued = 0');
        await database.execute(
            'CREATE VIEW IF NOT EXISTS `BarcodeDispense` AS SELECT SUM(quantityDispensed) AS quantity, regimen, barcode, siteCode FROM Refill\n      JOIN patient p ON p.uuID = patientId GROUP BY regimen, barcode, siteCode');
        await database.execute(
            'CREATE VIEW IF NOT EXISTS `BarcodeQuantity` AS SELECT SUM(quantity) AS quantity, regimen, barcode, siteCode FROM Inventory\n      GROUP BY regimen, barcode, siteCode');
        await database.execute(
            'CREATE VIEW IF NOT EXISTS `InventoryQuantity` AS select quantity, siteCode, regimen from Inventory');
        await database.execute(
            'CREATE VIEW IF NOT EXISTS `LastRefill` AS   WITH last_refill AS ( \n      SELECT * FROM (\n            SELECT patientId, date, dateNextRefill, ROW_NUMBER() OVER (PARTITION BY patientId \n            ORDER BY date DESC) rn FROM refill\n      ) r WHERE rn = 1\n  )\n  SELECT siteCode, givenName, familyName, hospitalNo, sex, dateOfBirth, date, \n    dateNextRefill FROM last_refill JOIN patient ON patientId = uuid ORDER BY givenName, familyName\n');
        await database.execute(
            'CREATE VIEW IF NOT EXISTS `InventoryAvailability` AS SELECT COUNT(quantity) > 0 AS isAvailable, siteCode FROM Inventory \n    GROUP BY siteCode');
        await database.execute(
            'CREATE VIEW IF NOT EXISTS `EstimatedRefill` AS WITH Estimated AS (\n\tSELECT * FROM (\n\t\tSELECT quantityDispensed, regimen, dateNextRefill, patientId, siteCode,\n\t\t\tROW_NUMBER() OVER(PARTITION BY patientId ORDER BY dateNextRefill DESC) rn \n\t\tFROM Refill JOIN Patient p ON patientId = p.uuid\t\n\t) e WHERE rn = 1\n)\nSELECT regimen, siteCode, SUM(quantityDispensed) qty, dateNextRefill \n  FROM Estimated GROUP BY regimen, siteCode, dateNextRefill\n');
        await database.execute(
            'CREATE VIEW IF NOT EXISTS `RefillInfo` AS   SELECT givenName, familyName, sex, dateOfBirth, quantityDispensed quantity, \n    hospitalNo, regimen, siteCode, dateNextRefill, date FROM Refill JOIN Patient \n    p ON patientId = p.uuid ORDER BY givenName, familyName, sex    \n');

        await callback?.onCreate?.call(database, version);
      },
    );
    return sqfliteDatabaseFactory.openDatabase(path, options: databaseOptions);
  }

  @override
  ClinicDao get clinicDao {
    return _clinicDaoInstance ??= _$ClinicDao(database, changeListener);
  }

  @override
  PatientDao get patientDao {
    return _patientDaoInstance ??= _$PatientDao(database, changeListener);
  }

  @override
  RefillDao get refillDao {
    return _refillDaoInstance ??= _$RefillDao(database, changeListener);
  }

  @override
  InventoryDao get inventoryDao {
    return _inventoryDaoInstance ??= _$InventoryDao(database, changeListener);
  }

  @override
  InventoryRequestDao get inventoryRequestDao {
    return _inventoryRequestDaoInstance ??=
        _$InventoryRequestDao(database, changeListener);
  }

  @override
  DevolveDao get devolveDao {
    return _devolveDaoInstance ??= _$DevolveDao(database, changeListener);
  }
}

class _$ClinicDao extends ClinicDao {
  _$ClinicDao(
    this.database,
    this.changeListener,
  )   : _queryAdapter = QueryAdapter(database, changeListener),
        _clinicDataInsertionAdapter = InsertionAdapter(
            database,
            'clinic',
            (ClinicData item) => <String, Object?>{
                  'id': item.id,
                  'systolic': item.systolic,
                  'diastolic': item.diastolic,
                  'weight': item.weight,
                  'temperature': item.temperature,
                  'patientId': item.patientId,
                  'date': _dateTimeConverter.encode(item.date),
                  'coughing':
                      item.coughing == null ? null : (item.coughing! ? 1 : 0),
                  'swelling':
                      item.swelling == null ? null : (item.swelling! ? 1 : 0),
                  'sweating':
                      item.sweating == null ? null : (item.sweating! ? 1 : 0),
                  'fever': item.fever == null ? null : (item.fever! ? 1 : 0),
                  'weightLoss': item.weightLoss == null
                      ? null
                      : (item.weightLoss! ? 1 : 0),
                  'tbReferred': item.tbReferred == null
                      ? null
                      : (item.tbReferred! ? 1 : 0),
                  'synced': item.synced ? 1 : 0
                },
            changeListener);

  final sqflite.DatabaseExecutor database;

  final StreamController<String> changeListener;

  final QueryAdapter _queryAdapter;

  final InsertionAdapter<ClinicData> _clinicDataInsertionAdapter;

  @override
  Future<List<ClinicData>> findAll() async {
    return _queryAdapter.queryList('SELECT * FROM Clinic',
        mapper: (Map<String, Object?> row) => ClinicData(
            row['id'] as int?,
            row['systolic'] as int?,
            row['diastolic'] as int?,
            row['weight'] as double?,
            row['temperature'] as double?,
            row['patientId'] as String,
            _dateTimeConverter.decode(row['date'] as int),
            row['coughing'] == null ? null : (row['coughing'] as int) != 0,
            row['swelling'] == null ? null : (row['swelling'] as int) != 0,
            row['sweating'] == null ? null : (row['sweating'] as int) != 0,
            row['fever'] == null ? null : (row['fever'] as int) != 0,
            row['weightLoss'] == null ? null : (row['weightLoss'] as int) != 0,
            row['tbReferred'] == null ? null : (row['tbReferred'] as int) != 0,
            (row['synced'] as int) != 0));
  }

  @override
  Future<List<ClinicData>> findUnSynced() async {
    return _queryAdapter.queryList('SELECT * FROM Clinic where synced = false',
        mapper: (Map<String, Object?> row) => ClinicData(
            row['id'] as int?,
            row['systolic'] as int?,
            row['diastolic'] as int?,
            row['weight'] as double?,
            row['temperature'] as double?,
            row['patientId'] as String,
            _dateTimeConverter.decode(row['date'] as int),
            row['coughing'] == null ? null : (row['coughing'] as int) != 0,
            row['swelling'] == null ? null : (row['swelling'] as int) != 0,
            row['sweating'] == null ? null : (row['sweating'] as int) != 0,
            row['fever'] == null ? null : (row['fever'] as int) != 0,
            row['weightLoss'] == null ? null : (row['weightLoss'] as int) != 0,
            row['tbReferred'] == null ? null : (row['tbReferred'] as int) != 0,
            (row['synced'] as int) != 0));
  }

  @override
  Stream<ClinicData?> findById(int id) {
    return _queryAdapter.queryStream('SELECT * FROM Clinic WHERE id = ?1',
        mapper: (Map<String, Object?> row) => ClinicData(
            row['id'] as int?,
            row['systolic'] as int?,
            row['diastolic'] as int?,
            row['weight'] as double?,
            row['temperature'] as double?,
            row['patientId'] as String,
            _dateTimeConverter.decode(row['date'] as int),
            row['coughing'] == null ? null : (row['coughing'] as int) != 0,
            row['swelling'] == null ? null : (row['swelling'] as int) != 0,
            row['sweating'] == null ? null : (row['sweating'] as int) != 0,
            row['fever'] == null ? null : (row['fever'] as int) != 0,
            row['weightLoss'] == null ? null : (row['weightLoss'] as int) != 0,
            row['tbReferred'] == null ? null : (row['tbReferred'] as int) != 0,
            (row['synced'] as int) != 0),
        arguments: [id],
        queryableName: 'Clinic',
        isView: false);
  }

  @override
  Future<List<ClinicData>> findByPatient(int patientId) async {
    return _queryAdapter.queryList('SELECT * FROM Clinic WHERE patientId = ?1',
        mapper: (Map<String, Object?> row) => ClinicData(
            row['id'] as int?,
            row['systolic'] as int?,
            row['diastolic'] as int?,
            row['weight'] as double?,
            row['temperature'] as double?,
            row['patientId'] as String,
            _dateTimeConverter.decode(row['date'] as int),
            row['coughing'] == null ? null : (row['coughing'] as int) != 0,
            row['swelling'] == null ? null : (row['swelling'] as int) != 0,
            row['sweating'] == null ? null : (row['sweating'] as int) != 0,
            row['fever'] == null ? null : (row['fever'] as int) != 0,
            row['weightLoss'] == null ? null : (row['weightLoss'] as int) != 0,
            row['tbReferred'] == null ? null : (row['tbReferred'] as int) != 0,
            (row['synced'] as int) != 0),
        arguments: [patientId]);
  }

  @override
  Future<void> deleteById(int id) async {
    await _queryAdapter
        .queryNoReturn('delete from Clinic where id = ?1', arguments: [id]);
  }

  @override
  Future<void> deleteAll() async {
    await _queryAdapter.queryNoReturn('DELETE FROM Clinic');
  }

  @override
  Future<bool?> hasUnSynced() async {
    return _queryAdapter.query(
        'SELECT COUNT(*) > 0 FROM Clinic WHERE synced = 0',
        mapper: (Map<String, Object?> row) => (row.values.first as int) != 0);
  }

  @override
  Future<void> updateAllSynced() async {
    await _queryAdapter.queryNoReturn('UPDATE Clinic SET synced = 1');
  }

  @override
  Future<void> insertRecord(ClinicData clinic) async {
    await _clinicDataInsertionAdapter.insert(clinic, OnConflictStrategy.abort);
  }
}

class _$PatientDao extends PatientDao {
  _$PatientDao(
    this.database,
    this.changeListener,
  )   : _queryAdapter = QueryAdapter(database),
        _patientInsertionAdapter = InsertionAdapter(
            database,
            'Patient',
            (Patient item) => <String, Object?>{
                  'id': item.id,
                  'givenName': item.givenName,
                  'familyName': item.familyName,
                  'hospitalNo': item.hospitalNo,
                  'uniqueId': item.uniqueId,
                  'dateOfBirth': _dateTimeConverter.encode(item.dateOfBirth),
                  'sex': item.sex,
                  'phoneNumber': item.phoneNumber,
                  'assignedRegimen': item.assignedRegimen,
                  'facility': item.facility,
                  'siteCode': item.siteCode,
                  'address': item.address,
                  'lastClinicVisit':
                      _dateTimeConverter.encode(item.lastClinicVisit),
                  'lastRefillDate':
                      _dateTimeConverter.encode(item.lastRefillDate),
                  'nextAppointmentDate':
                      _dateTimeConverter.encode(item.nextAppointmentDate),
                  'nextRefillDate':
                      _dateTimeConverter.encode(item.nextRefillDate),
                  'serviceDiscontinued': item.serviceDiscontinued ? 1 : 0,
                  'reasonDiscontinued': item.reasonDiscontinued,
                  'dateDiscontinued':
                      _dateTimeConverter.encode(item.dateDiscontinued),
                  'uuid': item.uuid,
                  'synced': item.synced ? 1 : 0
                }),
        _patientUpdateAdapter = UpdateAdapter(
            database,
            'Patient',
            ['id'],
            (Patient item) => <String, Object?>{
                  'id': item.id,
                  'givenName': item.givenName,
                  'familyName': item.familyName,
                  'hospitalNo': item.hospitalNo,
                  'uniqueId': item.uniqueId,
                  'dateOfBirth': _dateTimeConverter.encode(item.dateOfBirth),
                  'sex': item.sex,
                  'phoneNumber': item.phoneNumber,
                  'assignedRegimen': item.assignedRegimen,
                  'facility': item.facility,
                  'siteCode': item.siteCode,
                  'address': item.address,
                  'lastClinicVisit':
                      _dateTimeConverter.encode(item.lastClinicVisit),
                  'lastRefillDate':
                      _dateTimeConverter.encode(item.lastRefillDate),
                  'nextAppointmentDate':
                      _dateTimeConverter.encode(item.nextAppointmentDate),
                  'nextRefillDate':
                      _dateTimeConverter.encode(item.nextRefillDate),
                  'serviceDiscontinued': item.serviceDiscontinued ? 1 : 0,
                  'reasonDiscontinued': item.reasonDiscontinued,
                  'dateDiscontinued':
                      _dateTimeConverter.encode(item.dateDiscontinued),
                  'uuid': item.uuid,
                  'synced': item.synced ? 1 : 0
                });

  final sqflite.DatabaseExecutor database;

  final StreamController<String> changeListener;

  final QueryAdapter _queryAdapter;

  final InsertionAdapter<Patient> _patientInsertionAdapter;

  final UpdateAdapter<Patient> _patientUpdateAdapter;

  @override
  Future<List<Patient>> findAll(String siteCode) async {
    return _queryAdapter.queryList(
        'SELECT * FROM Patient where siteCode = ?1 and serviceDiscontinued = 0         order by givenName, familyName limit 10',
        mapper: (Map<String, Object?> row) => Patient(row['id'] as int?, row['givenName'] as String, row['familyName'] as String, row['hospitalNo'] as String, row['uniqueId'] as String, _dateTimeConverter.decode(row['dateOfBirth'] as int), row['sex'] as String, row['phoneNumber'] as String, row['assignedRegimen'] as String, row['facility'] as String, row['siteCode'] as String, row['address'] as String, _dateTimeConverter.decode(row['lastClinicVisit'] as int), _dateTimeConverter.decode(row['lastRefillDate'] as int), _dateTimeConverter.decode(row['nextAppointmentDate'] as int), _dateTimeConverter.decode(row['nextRefillDate'] as int), (row['serviceDiscontinued'] as int) != 0, row['reasonDiscontinued'] as String, _dateTimeConverter.decode(row['dateDiscontinued'] as int), row['uuid'] as String, (row['synced'] as int) != 0),
        arguments: [siteCode]);
  }

  @override
  Future<List<Patient>> findByKeyword(
    String siteCode,
    String keyword,
  ) async {
    return _queryAdapter.queryList(
        'SELECT * FROM Patient where siteCode = ?1 and serviceDiscontinued = 0        and (lower(givenName) like lower(?2) or lower(familyName) like        lower(?2) or lower(hospitalNo) like lower(?2)) order by givenName,        familyName limit 10',
        mapper: (Map<String, Object?> row) => Patient(row['id'] as int?, row['givenName'] as String, row['familyName'] as String, row['hospitalNo'] as String, row['uniqueId'] as String, _dateTimeConverter.decode(row['dateOfBirth'] as int), row['sex'] as String, row['phoneNumber'] as String, row['assignedRegimen'] as String, row['facility'] as String, row['siteCode'] as String, row['address'] as String, _dateTimeConverter.decode(row['lastClinicVisit'] as int), _dateTimeConverter.decode(row['lastRefillDate'] as int), _dateTimeConverter.decode(row['nextAppointmentDate'] as int), _dateTimeConverter.decode(row['nextRefillDate'] as int), (row['serviceDiscontinued'] as int) != 0, row['reasonDiscontinued'] as String, _dateTimeConverter.decode(row['dateDiscontinued'] as int), row['uuid'] as String, (row['synced'] as int) != 0),
        arguments: [siteCode, keyword]);
  }

  @override
  Future<List<Patient>> findDiscontinued(String siteCode) async {
    return _queryAdapter.queryList(
        'SELECT * FROM Patient where siteCode = ?1 and serviceDiscontinued = 1',
        mapper: (Map<String, Object?> row) => Patient(
            row['id'] as int?,
            row['givenName'] as String,
            row['familyName'] as String,
            row['hospitalNo'] as String,
            row['uniqueId'] as String,
            _dateTimeConverter.decode(row['dateOfBirth'] as int),
            row['sex'] as String,
            row['phoneNumber'] as String,
            row['assignedRegimen'] as String,
            row['facility'] as String,
            row['siteCode'] as String,
            row['address'] as String,
            _dateTimeConverter.decode(row['lastClinicVisit'] as int),
            _dateTimeConverter.decode(row['lastRefillDate'] as int),
            _dateTimeConverter.decode(row['nextAppointmentDate'] as int),
            _dateTimeConverter.decode(row['nextRefillDate'] as int),
            (row['serviceDiscontinued'] as int) != 0,
            row['reasonDiscontinued'] as String,
            _dateTimeConverter.decode(row['dateDiscontinued'] as int),
            row['uuid'] as String,
            (row['synced'] as int) != 0),
        arguments: [siteCode]);
  }

  @override
  Future<Patient?> findById(int id) async {
    return _queryAdapter.query('SELECT * FROM Patient WHERE id = ?1',
        mapper: (Map<String, Object?> row) => Patient(
            row['id'] as int?,
            row['givenName'] as String,
            row['familyName'] as String,
            row['hospitalNo'] as String,
            row['uniqueId'] as String,
            _dateTimeConverter.decode(row['dateOfBirth'] as int),
            row['sex'] as String,
            row['phoneNumber'] as String,
            row['assignedRegimen'] as String,
            row['facility'] as String,
            row['siteCode'] as String,
            row['address'] as String,
            _dateTimeConverter.decode(row['lastClinicVisit'] as int),
            _dateTimeConverter.decode(row['lastRefillDate'] as int),
            _dateTimeConverter.decode(row['nextAppointmentDate'] as int),
            _dateTimeConverter.decode(row['nextRefillDate'] as int),
            (row['serviceDiscontinued'] as int) != 0,
            row['reasonDiscontinued'] as String,
            _dateTimeConverter.decode(row['dateDiscontinued'] as int),
            row['uuid'] as String,
            (row['synced'] as int) != 0),
        arguments: [id]);
  }

  @override
  Future<Patient?> findByUniqueId(String uniqueId) async {
    return _queryAdapter.query('SELECT * FROM Patient WHERE uniqueId = ?1',
        mapper: (Map<String, Object?> row) => Patient(
            row['id'] as int?,
            row['givenName'] as String,
            row['familyName'] as String,
            row['hospitalNo'] as String,
            row['uniqueId'] as String,
            _dateTimeConverter.decode(row['dateOfBirth'] as int),
            row['sex'] as String,
            row['phoneNumber'] as String,
            row['assignedRegimen'] as String,
            row['facility'] as String,
            row['siteCode'] as String,
            row['address'] as String,
            _dateTimeConverter.decode(row['lastClinicVisit'] as int),
            _dateTimeConverter.decode(row['lastRefillDate'] as int),
            _dateTimeConverter.decode(row['nextAppointmentDate'] as int),
            _dateTimeConverter.decode(row['nextRefillDate'] as int),
            (row['serviceDiscontinued'] as int) != 0,
            row['reasonDiscontinued'] as String,
            _dateTimeConverter.decode(row['dateDiscontinued'] as int),
            row['uuid'] as String,
            (row['synced'] as int) != 0),
        arguments: [uniqueId]);
  }

  @override
  Future<void> updateSynced(int id) async {
    await _queryAdapter.queryNoReturn(
        'UPDATE Patient set synced = true WHERE id = ?1',
        arguments: [id]);
  }

  @override
  Future<List<AssignedRegimen>> listAssignedRegimen(String siteCode) async {
    return _queryAdapter.queryList(
        'SELECT * FROM AssignedRegimen WHERE siteCode = ?1',
        mapper: (Map<String, Object?> row) => AssignedRegimen(
            row['assignedRegimen'] as String, row['siteCode'] as String),
        arguments: [siteCode]);
  }

  @override
  Future<List<LastRefill>> listMissedRefill(
    String siteCode,
    DateTime start,
    DateTime end,
  ) async {
    return _queryAdapter.queryList(
        'SELECT * FROM LastRefill WHERE siteCode = ?1 AND dateNextRefill          BETWEEN ?2 AND ?3',
        mapper: (Map<String, Object?> row) => LastRefill(row['siteCode'] as String, row['givenName'] as String, row['familyName'] as String, row['sex'] as String, _dateTimeConverter.decode(row['dateOfBirth'] as int), _dateTimeConverter.decode(row['date'] as int), _dateTimeConverter.decode(row['dateNextRefill'] as int), row['hospitalNo'] as String),
        arguments: [
          siteCode,
          _dateTimeConverter.encode(start),
          _dateTimeConverter.encode(end)
        ]);
  }

  @override
  Future<void> discontinueService(
    int id,
    DateTime dateDiscontinued,
    String reasonDiscontinued,
  ) async {
    await _queryAdapter.queryNoReturn(
        'Update Patient set serviceDiscontinued = true, dateDiscontinued =        ?2, reasonDiscontinued = ?3 where id = ?1',
        arguments: [
          id,
          _dateTimeConverter.encode(dateDiscontinued),
          reasonDiscontinued
        ]);
  }

  @override
  Future<void> deleteAll() async {
    await _queryAdapter.queryNoReturn('delete from Patient');
  }

  @override
  Future<int> insertRecord(Patient patient) {
    return _patientInsertionAdapter.insertAndReturnId(
        patient, OnConflictStrategy.abort);
  }

  @override
  Future<void> updateRecord(Patient patient) async {
    await _patientUpdateAdapter.update(patient, OnConflictStrategy.abort);
  }
}

class _$RefillDao extends RefillDao {
  _$RefillDao(
    this.database,
    this.changeListener,
  )   : _queryAdapter = QueryAdapter(database, changeListener),
        _refillInsertionAdapter = InsertionAdapter(
            database,
            'Refill',
            (Refill item) => <String, Object?>{
                  'id': item.id,
                  'date': _dateTimeConverter.encode(item.date),
                  'regimen': item.regimen,
                  'patientId': item.patientId,
                  'quantityPrescribed': item.quantityPrescribed,
                  'quantityDispensed': item.quantityDispensed,
                  'dateNextRefill':
                      _dateTimeConverter.encode(item.dateNextRefill),
                  'synced': item.synced ? 1 : 0,
                  'missedDoses': item.missedDoses == null
                      ? null
                      : (item.missedDoses! ? 1 : 0),
                  'adverseIssues': item.adverseIssues == null
                      ? null
                      : (item.adverseIssues! ? 1 : 0),
                  'barcode': item.barcode,
                  'batchIssuanceId': item.batchIssuanceId
                },
            changeListener),
        _refillUpdateAdapter = UpdateAdapter(
            database,
            'Refill',
            ['id'],
            (Refill item) => <String, Object?>{
                  'id': item.id,
                  'date': _dateTimeConverter.encode(item.date),
                  'regimen': item.regimen,
                  'patientId': item.patientId,
                  'quantityPrescribed': item.quantityPrescribed,
                  'quantityDispensed': item.quantityDispensed,
                  'dateNextRefill':
                      _dateTimeConverter.encode(item.dateNextRefill),
                  'synced': item.synced ? 1 : 0,
                  'missedDoses': item.missedDoses == null
                      ? null
                      : (item.missedDoses! ? 1 : 0),
                  'adverseIssues': item.adverseIssues == null
                      ? null
                      : (item.adverseIssues! ? 1 : 0),
                  'barcode': item.barcode,
                  'batchIssuanceId': item.batchIssuanceId
                },
            changeListener);

  final sqflite.DatabaseExecutor database;

  final StreamController<String> changeListener;

  final QueryAdapter _queryAdapter;

  final InsertionAdapter<Refill> _refillInsertionAdapter;

  final UpdateAdapter<Refill> _refillUpdateAdapter;

  @override
  Future<List<Refill>> findAll() async {
    return _queryAdapter.queryList('SELECT * FROM Refill',
        mapper: (Map<String, Object?> row) => Refill(
            row['id'] as int?,
            _dateTimeConverter.decode(row['date'] as int),
            row['regimen'] as String,
            row['patientId'] as String,
            row['quantityPrescribed'] as int,
            row['quantityDispensed'] as int,
            _dateTimeConverter.decode(row['dateNextRefill'] as int),
            row['missedDoses'] == null
                ? null
                : (row['missedDoses'] as int) != 0,
            row['adverseIssues'] == null
                ? null
                : (row['adverseIssues'] as int) != 0,
            row['barcode'] as String?,
            row['batchIssuanceId'] as String?,
            (row['synced'] as int) != 0));
  }

  @override
  Stream<Refill?> findById(int id) {
    return _queryAdapter.queryStream('SELECT * FROM Refill WHERE id = ?1',
        mapper: (Map<String, Object?> row) => Refill(
            row['id'] as int?,
            _dateTimeConverter.decode(row['date'] as int),
            row['regimen'] as String,
            row['patientId'] as String,
            row['quantityPrescribed'] as int,
            row['quantityDispensed'] as int,
            _dateTimeConverter.decode(row['dateNextRefill'] as int),
            row['missedDoses'] == null
                ? null
                : (row['missedDoses'] as int) != 0,
            row['adverseIssues'] == null
                ? null
                : (row['adverseIssues'] as int) != 0,
            row['barcode'] as String?,
            row['batchIssuanceId'] as String?,
            (row['synced'] as int) != 0),
        arguments: [id],
        queryableName: 'Refill',
        isView: false);
  }

  @override
  Future<List<Refill>> findUnSynced() async {
    return _queryAdapter.queryList('SELECT * FROM Refill where synced = false',
        mapper: (Map<String, Object?> row) => Refill(
            row['id'] as int?,
            _dateTimeConverter.decode(row['date'] as int),
            row['regimen'] as String,
            row['patientId'] as String,
            row['quantityPrescribed'] as int,
            row['quantityDispensed'] as int,
            _dateTimeConverter.decode(row['dateNextRefill'] as int),
            row['missedDoses'] == null
                ? null
                : (row['missedDoses'] as int) != 0,
            row['adverseIssues'] == null
                ? null
                : (row['adverseIssues'] as int) != 0,
            row['barcode'] as String?,
            row['batchIssuanceId'] as String?,
            (row['synced'] as int) != 0));
  }

  @override
  Future<List<Refill>> findByPatient(String patientId) async {
    return _queryAdapter.queryList('SELECT * FROM Refill WHERE patientId = ?1',
        mapper: (Map<String, Object?> row) => Refill(
            row['id'] as int?,
            _dateTimeConverter.decode(row['date'] as int),
            row['regimen'] as String,
            row['patientId'] as String,
            row['quantityPrescribed'] as int,
            row['quantityDispensed'] as int,
            _dateTimeConverter.decode(row['dateNextRefill'] as int),
            row['missedDoses'] == null
                ? null
                : (row['missedDoses'] as int) != 0,
            row['adverseIssues'] == null
                ? null
                : (row['adverseIssues'] as int) != 0,
            row['barcode'] as String?,
            row['batchIssuanceId'] as String?,
            (row['synced'] as int) != 0),
        arguments: [patientId]);
  }

  @override
  Future<List<Refill>> findByPatientAndDate(
    int patientId,
    DateTime date,
  ) async {
    return _queryAdapter.queryList(
        'SELECT * FROM Refill WHERE patientId = ?1 AND date = ?2',
        mapper: (Map<String, Object?> row) => Refill(
            row['id'] as int?,
            _dateTimeConverter.decode(row['date'] as int),
            row['regimen'] as String,
            row['patientId'] as String,
            row['quantityPrescribed'] as int,
            row['quantityDispensed'] as int,
            _dateTimeConverter.decode(row['dateNextRefill'] as int),
            row['missedDoses'] == null
                ? null
                : (row['missedDoses'] as int) != 0,
            row['adverseIssues'] == null
                ? null
                : (row['adverseIssues'] as int) != 0,
            row['barcode'] as String?,
            row['batchIssuanceId'] as String?,
            (row['synced'] as int) != 0),
        arguments: [patientId, _dateTimeConverter.encode(date)]);
  }

  @override
  Future<void> deleteOlderThan(DateTime date) async {
    await _queryAdapter.queryNoReturn('DELETE FROM Refill WHERE date <= ?1',
        arguments: [_dateTimeConverter.encode(date)]);
  }

  @override
  Future<bool?> hasUnSynced() async {
    return _queryAdapter.query(
        'SELECT COUNT(*) > 0 FROM Refill WHERE synced = 0',
        mapper: (Map<String, Object?> row) => (row.values.first as int) != 0);
  }

  @override
  Future<void> updateAllSynced() async {
    await _queryAdapter.queryNoReturn('UPDATE Refill SET synced = 1');
  }

  @override
  Future<void> deleteById(int id) async {
    await _queryAdapter
        .queryNoReturn('delete from Refill where id = ?1', arguments: [id]);
  }

  @override
  Future<List<EstimatedRefill>> estimatedRefill(
    String siteCode,
    DateTime start,
    DateTime end,
  ) async {
    return _queryAdapter.queryList(
        'SELECT regimen, siteCode, SUM(qty) qty, 1 AS dateNextRefill FROM            EstimatedRefill WHERE siteCode = ?1 AND dateNextRefill BETWEEN            ?2 and ?3 GROUP BY regimen ORDER BY regimen, siteCode',
        mapper: (Map<String, Object?> row) => EstimatedRefill(row['siteCode'] as String, row['regimen'] as String, row['qty'] as int, _dateTimeConverter.decode(row['dateNextRefill'] as int)),
        arguments: [
          siteCode,
          _dateTimeConverter.encode(start),
          _dateTimeConverter.encode(end)
        ]);
  }

  @override
  Future<List<RefillInfo>> listRefillInfo(
    String siteCode,
    DateTime start,
    DateTime end,
  ) async {
    return _queryAdapter.queryList(
        'SELECT * FROM RefillInfo WHERE siteCode = ?1 AND date BETWEEN          ?2 and ?3 ORDER BY givenName, familyName',
        mapper: (Map<String, Object?> row) => RefillInfo(row['siteCode'] as String, row['familyName'] as String, row['givenName'] as String, row['quantity'] as int, _dateTimeConverter.decode(row['date'] as int), _dateTimeConverter.decode(row['dateNextRefill'] as int), _dateTimeConverter.decode(row['dateOfBirth'] as int), row['regimen'] as String, row['sex'] as String, row['hospitalNo'] as String),
        arguments: [
          siteCode,
          _dateTimeConverter.encode(start),
          _dateTimeConverter.encode(end)
        ]);
  }

  @override
  Future<BarcodeDispense?> barcodeQuantity(
    String siteCode,
    String regimen,
    String barcode,
  ) async {
    return _queryAdapter.query(
        'SELECT * FROM BarcodeDispense WHERE siteCode = ?1 AND regimen = ?2     AND barcode = ?3',
        mapper: (Map<String, Object?> row) => BarcodeDispense(row['quantity'] as int, row['siteCode'] as String, row['regimen'] as String, row['barcode'] as String),
        arguments: [siteCode, regimen, barcode]);
  }

  @override
  Future<void> insertRecord(Refill refill) async {
    await _refillInsertionAdapter.insert(refill, OnConflictStrategy.abort);
  }

  @override
  Future<int> updateRecord(Refill refill) {
    return _refillUpdateAdapter.updateAndReturnChangedRows(
        refill, OnConflictStrategy.abort);
  }
}

class _$InventoryDao extends InventoryDao {
  _$InventoryDao(
    this.database,
    this.changeListener,
  )   : _queryAdapter = QueryAdapter(database, changeListener),
        _inventoryInsertionAdapter = InsertionAdapter(
            database,
            'Inventory',
            (Inventory item) => <String, Object?>{
                  'id': item.id,
                  'uniqueId': item.uniqueId,
                  'reference': item.reference,
                  'regimen': item.regimen,
                  'quantity': item.quantity,
                  'batchNo': item.batchNo,
                  'barcode': item.barcode,
                  'siteCode': item.siteCode,
                  'expiryDate': _dateTimeConverter.encode(item.expiryDate),
                  'batchIssuanceId': item.batchIssuanceId,
                  'acknowledged': item.acknowledged ? 1 : 0,
                  'synced': item.synced ? 1 : 0
                },
            changeListener),
        _inventoryUpdateAdapter = UpdateAdapter(
            database,
            'Inventory',
            ['id'],
            (Inventory item) => <String, Object?>{
                  'id': item.id,
                  'uniqueId': item.uniqueId,
                  'reference': item.reference,
                  'regimen': item.regimen,
                  'quantity': item.quantity,
                  'batchNo': item.batchNo,
                  'barcode': item.barcode,
                  'siteCode': item.siteCode,
                  'expiryDate': _dateTimeConverter.encode(item.expiryDate),
                  'batchIssuanceId': item.batchIssuanceId,
                  'acknowledged': item.acknowledged ? 1 : 0,
                  'synced': item.synced ? 1 : 0
                },
            changeListener);

  final sqflite.DatabaseExecutor database;

  final StreamController<String> changeListener;

  final QueryAdapter _queryAdapter;

  final InsertionAdapter<Inventory> _inventoryInsertionAdapter;

  final UpdateAdapter<Inventory> _inventoryUpdateAdapter;

  @override
  Future<List<Inventory>> findAll(String siteCode) async {
    return _queryAdapter.queryList(
        'SELECT * FROM Inventory where siteCode = ?1 order by expiryDate',
        mapper: (Map<String, Object?> row) => Inventory(
            row['id'] as int?,
            row['uniqueId'] as String,
            row['reference'] as String,
            row['regimen'] as String,
            row['quantity'] as int,
            (row['acknowledged'] as int) != 0,
            row['batchNo'] as String,
            row['barcode'] as String,
            row['siteCode'] as String,
            _dateTimeConverter.decode(row['expiryDate'] as int),
            row['batchIssuanceId'] as String),
        arguments: [siteCode]);
  }

  @override
  Future<List<Inventory>> findByRegimen(
    String siteCode,
    String regimen,
  ) async {
    return _queryAdapter.queryList(
        'SELECT * FROM Inventory where siteCode = ?1 and regimen = ?2 order by expiryDate',
        mapper: (Map<String, Object?> row) => Inventory(row['id'] as int?, row['uniqueId'] as String, row['reference'] as String, row['regimen'] as String, row['quantity'] as int, (row['acknowledged'] as int) != 0, row['batchNo'] as String, row['barcode'] as String, row['siteCode'] as String, _dateTimeConverter.decode(row['expiryDate'] as int), row['batchIssuanceId'] as String),
        arguments: [siteCode, regimen]);
  }

  @override
  Stream<Inventory?> findById(int id) {
    return _queryAdapter.queryStream('SELECT * FROM Inventory WHERE id = ?1',
        mapper: (Map<String, Object?> row) => Inventory(
            row['id'] as int?,
            row['uniqueId'] as String,
            row['reference'] as String,
            row['regimen'] as String,
            row['quantity'] as int,
            (row['acknowledged'] as int) != 0,
            row['batchNo'] as String,
            row['barcode'] as String,
            row['siteCode'] as String,
            _dateTimeConverter.decode(row['expiryDate'] as int),
            row['batchIssuanceId'] as String),
        arguments: [id],
        queryableName: 'Inventory',
        isView: false);
  }

  @override
  Stream<Inventory?> findByUniqueId(String uniqueId) {
    return _queryAdapter.queryStream(
        'SELECT * FROM Inventory WHERE uniqueId = ?1',
        mapper: (Map<String, Object?> row) => Inventory(
            row['id'] as int?,
            row['uniqueId'] as String,
            row['reference'] as String,
            row['regimen'] as String,
            row['quantity'] as int,
            (row['acknowledged'] as int) != 0,
            row['batchNo'] as String,
            row['barcode'] as String,
            row['siteCode'] as String,
            _dateTimeConverter.decode(row['expiryDate'] as int),
            row['batchIssuanceId'] as String),
        arguments: [uniqueId],
        queryableName: 'Inventory',
        isView: false);
  }

  @override
  Future<List<Inventory>> findByUniqueIdAndRegimen(
    String uniqueId,
    String regimen,
  ) async {
    return _queryAdapter.queryList(
        'SELECT * FROM Inventory WHERE uniqueId = ?1 and regimen = ?2',
        mapper: (Map<String, Object?> row) => Inventory(
            row['id'] as int?,
            row['uniqueId'] as String,
            row['reference'] as String,
            row['regimen'] as String,
            row['quantity'] as int,
            (row['acknowledged'] as int) != 0,
            row['batchNo'] as String,
            row['barcode'] as String,
            row['siteCode'] as String,
            _dateTimeConverter.decode(row['expiryDate'] as int),
            row['batchIssuanceId'] as String),
        arguments: [uniqueId, regimen]);
  }

  @override
  Future<void> updateQuantity(
    int id,
    int quantity,
  ) async {
    await _queryAdapter.queryNoReturn(
        'Update Inventory set quantity = ?2 WHERE id = ?1',
        arguments: [id, quantity]);
  }

  @override
  Future<InventoryAvailability?> checkAvailability(String siteCode) async {
    return _queryAdapter.query(
        'SELECT * FROM InventoryAvailability WHERE siteCode = ?1',
        mapper: (Map<String, Object?> row) => InventoryAvailability(
            (row['isAvailable'] as int) != 0, row['siteCode'] as String),
        arguments: [siteCode]);
  }

  @override
  Future<BarcodeQuantity?> barcodeQuantity(
    String siteCode,
    String regimen,
    String barcode,
  ) async {
    return _queryAdapter.query(
        'SELECT * FROM BarcodeQuantity WHERE siteCode = ?1 AND regimen = ?2     AND barcode = ?3',
        mapper: (Map<String, Object?> row) => BarcodeQuantity(row['quantity'] as int, row['siteCode'] as String, row['regimen'] as String, row['barcode'] as String),
        arguments: [siteCode, regimen, barcode]);
  }

  @override
  Future<int?> issuedQuantity(
    String siteCode,
    String uniqueId,
  ) async {
    return _queryAdapter.query(
        'SELECT SUM(quantity) FROM Inventory WHERE siteCode = ?1 AND uniqueId = ?2',
        mapper: (Map<String, Object?> row) => row.values.first as int,
        arguments: [siteCode, uniqueId]);
  }

  @override
  Future<Inventory?> getNonZeroInventory(
    String siteCode,
    String regimen,
  ) async {
    return _queryAdapter.query(
        'SELECT * FROM Inventory WHERE siteCode = ?1 and regimen = ?2            and quantity > 0 order by expiryDate limit 1',
        mapper: (Map<String, Object?> row) => Inventory(row['id'] as int?, row['uniqueId'] as String, row['reference'] as String, row['regimen'] as String, row['quantity'] as int, (row['acknowledged'] as int) != 0, row['batchNo'] as String, row['barcode'] as String, row['siteCode'] as String, _dateTimeConverter.decode(row['expiryDate'] as int), row['batchIssuanceId'] as String),
        arguments: [siteCode, regimen]);
  }

  @override
  Future<List<InventoryQuantity>> selectInventoryQuantity(
    String siteCode,
    String regimen,
  ) async {
    return _queryAdapter.queryList(
        'select * from InventoryQuantity where siteCode = ?1 and regimen = ?2',
        mapper: (Map<String, Object?> row) => InventoryQuantity(
            row['quantity'] as int,
            row['siteCode'] as String,
            row['regimen'] as String),
        arguments: [siteCode, regimen]);
  }

  @override
  Future<void> acknowledge(String reference) async {
    await _queryAdapter.queryNoReturn(
        'UPDATE Inventory SET acknowledged = 1 WHERE reference = ?1',
        arguments: [reference]);
  }

  @override
  Future<bool?> hasUnSynced() async {
    return _queryAdapter.query(
        'SELECT COUNT(*) > 0 FROM Inventory WHERE synced = 0',
        mapper: (Map<String, Object?> row) => (row.values.first as int) != 0);
  }

  @override
  Future<List<Inventory>> findUnSynced() async {
    return _queryAdapter.queryList('SELECT * FROM Inventory WHERE synced = 0',
        mapper: (Map<String, Object?> row) => Inventory(
            row['id'] as int?,
            row['uniqueId'] as String,
            row['reference'] as String,
            row['regimen'] as String,
            row['quantity'] as int,
            (row['acknowledged'] as int) != 0,
            row['batchNo'] as String,
            row['barcode'] as String,
            row['siteCode'] as String,
            _dateTimeConverter.decode(row['expiryDate'] as int),
            row['batchIssuanceId'] as String));
  }

  @override
  Future<void> updateAllSynced() async {
    await _queryAdapter.queryNoReturn(
        'UPDATE Inventory SET synced = 1 WHERE acknowledged = 1');
  }

  @override
  Future<int> insertRecord(Inventory inventory) {
    return _inventoryInsertionAdapter.insertAndReturnId(
        inventory, OnConflictStrategy.abort);
  }

  @override
  Future<void> updateRecord(Inventory inventory) async {
    await _inventoryUpdateAdapter.update(inventory, OnConflictStrategy.abort);
  }
}

class _$InventoryRequestDao extends InventoryRequestDao {
  _$InventoryRequestDao(
    this.database,
    this.changeListener,
  )   : _queryAdapter = QueryAdapter(database, changeListener),
        _inventoryRequestInsertionAdapter = InsertionAdapter(
            database,
            'InventoryRequest',
            (InventoryRequest item) => <String, Object?>{
                  'id': item.id,
                  'uniqueId': item.uniqueId,
                  'regimen': item.regimen,
                  'quantity': item.quantity,
                  'quantityFulfilled': item.quantityFulfilled,
                  'siteCode': item.siteCode,
                  'date': _dateTimeConverter.encode(item.date),
                  'synced': item.synced ? 1 : 0
                },
            changeListener),
        _inventoryRequestUpdateAdapter = UpdateAdapter(
            database,
            'InventoryRequest',
            ['id'],
            (InventoryRequest item) => <String, Object?>{
                  'id': item.id,
                  'uniqueId': item.uniqueId,
                  'regimen': item.regimen,
                  'quantity': item.quantity,
                  'quantityFulfilled': item.quantityFulfilled,
                  'siteCode': item.siteCode,
                  'date': _dateTimeConverter.encode(item.date),
                  'synced': item.synced ? 1 : 0
                },
            changeListener);

  final sqflite.DatabaseExecutor database;

  final StreamController<String> changeListener;

  final QueryAdapter _queryAdapter;

  final InsertionAdapter<InventoryRequest> _inventoryRequestInsertionAdapter;

  final UpdateAdapter<InventoryRequest> _inventoryRequestUpdateAdapter;

  @override
  Future<List<InventoryRequest>> findAll(String siteCode) async {
    return _queryAdapter.queryList(
        'SELECT * FROM InventoryRequest where siteCode = ?1 order by date',
        mapper: (Map<String, Object?> row) => InventoryRequest(
            row['id'] as int?,
            row['uniqueId'] as String,
            row['regimen'] as String,
            row['quantity'] as int,
            row['siteCode'] as String,
            _dateTimeConverter.decode(row['date'] as int),
            (row['synced'] as int) != 0,
            row['quantityFulfilled'] as int),
        arguments: [siteCode]);
  }

  @override
  Future<List<InventoryRequest>> findRegimen(
    String siteCode,
    String regimen,
  ) async {
    return _queryAdapter.queryList(
        'SELECT * FROM InventoryRequest where siteCode = ?1 and regimen = ?2 order by date',
        mapper: (Map<String, Object?> row) => InventoryRequest(row['id'] as int?, row['uniqueId'] as String, row['regimen'] as String, row['quantity'] as int, row['siteCode'] as String, _dateTimeConverter.decode(row['date'] as int), (row['synced'] as int) != 0, row['quantityFulfilled'] as int),
        arguments: [siteCode, regimen]);
  }

  @override
  Stream<InventoryRequest?> findById(int id) {
    return _queryAdapter.queryStream(
        'SELECT * FROM InventoryRequest WHERE id = ?1',
        mapper: (Map<String, Object?> row) => InventoryRequest(
            row['id'] as int?,
            row['uniqueId'] as String,
            row['regimen'] as String,
            row['quantity'] as int,
            row['siteCode'] as String,
            _dateTimeConverter.decode(row['date'] as int),
            (row['synced'] as int) != 0,
            row['quantityFulfilled'] as int),
        arguments: [id],
        queryableName: 'InventoryRequest',
        isView: false);
  }

  @override
  Future<List<InventoryRequest>> findByUniqueId(String uniqueId) async {
    return _queryAdapter.queryList(
        'SELECT * FROM InventoryRequest WHERE uniqueId = ?1',
        mapper: (Map<String, Object?> row) => InventoryRequest(
            row['id'] as int?,
            row['uniqueId'] as String,
            row['regimen'] as String,
            row['quantity'] as int,
            row['siteCode'] as String,
            _dateTimeConverter.decode(row['date'] as int),
            (row['synced'] as int) != 0,
            row['quantityFulfilled'] as int),
        arguments: [uniqueId]);
  }

  @override
  Future<void> fulfilled(
    int bottles,
    String uniqueId,
  ) async {
    await _queryAdapter.queryNoReturn(
        'Update InventoryRequest SET quantityFulfilled = quantityFulfilled  + ?1      WHERE uniqueId = ?2',
        arguments: [bottles, uniqueId]);
  }

  @override
  Future<void> deleteById(int id) async {
    await _queryAdapter
        .queryNoReturn('delete from Inventory where id = ?1', arguments: [id]);
  }

  @override
  Future<bool?> hasUnSynced() async {
    return _queryAdapter.query(
        'SELECT COUNT(*) > 0 FROM InventoryRequest WHERE synced = 0',
        mapper: (Map<String, Object?> row) => (row.values.first as int) != 0);
  }

  @override
  Future<void> updateAllSynced() async {
    await _queryAdapter.queryNoReturn('UPDATE InventoryRequest SET synced = 1');
  }

  @override
  Future<List<InventoryRequest>> findUnSynced() async {
    return _queryAdapter.queryList(
        'SELECT * FROM InventoryRequest where synced = 0',
        mapper: (Map<String, Object?> row) => InventoryRequest(
            row['id'] as int?,
            row['uniqueId'] as String,
            row['regimen'] as String,
            row['quantity'] as int,
            row['siteCode'] as String,
            _dateTimeConverter.decode(row['date'] as int),
            (row['synced'] as int) != 0,
            row['quantityFulfilled'] as int));
  }

  @override
  Future<int> insertRecord(InventoryRequest inventory) {
    return _inventoryRequestInsertionAdapter.insertAndReturnId(
        inventory, OnConflictStrategy.abort);
  }

  @override
  Future<void> updateRecord(InventoryRequest inventory) async {
    await _inventoryRequestUpdateAdapter.update(
        inventory, OnConflictStrategy.abort);
  }
}

class _$DevolveDao extends DevolveDao {
  _$DevolveDao(
    this.database,
    this.changeListener,
  )   : _queryAdapter = QueryAdapter(database),
        _devolveInsertionAdapter = InsertionAdapter(
            database,
            'Devolve',
            (Devolve item) => <String, Object?>{
                  'id': item.id,
                  'reasonDiscontinued': item.reasonDiscontinued,
                  'date': _dateTimeConverter.encode(item.date),
                  'outletCode': item.outletCode,
                  'patientId': item.patientId,
                  'synced': item.synced == null ? null : (item.synced! ? 1 : 0)
                });

  final sqflite.DatabaseExecutor database;

  final StreamController<String> changeListener;

  final QueryAdapter _queryAdapter;

  final InsertionAdapter<Devolve> _devolveInsertionAdapter;

  @override
  Future<List<Devolve>> findUnSynced() async {
    return _queryAdapter.queryList('SELECT * FROM Devolve WHERE synced = 0',
        mapper: (Map<String, Object?> row) => Devolve(
            id: row['id'] as int?,
            reasonDiscontinued: row['reasonDiscontinued'] as String?,
            date: _dateTimeConverter.decode(row['date'] as int),
            outletCode: row['outletCode'] as String,
            patientId: row['patientId'] as String,
            synced:
                row['synced'] == null ? null : (row['synced'] as int) != 0));
  }

  @override
  Future<Devolve?> findByPatient(String patientId) async {
    return _queryAdapter.query(
        'SELECT * FROM Devolve WHERE patientId = ?1 ORDER BY date DESC LIMIT 1',
        mapper: (Map<String, Object?> row) => Devolve(
            id: row['id'] as int?,
            reasonDiscontinued: row['reasonDiscontinued'] as String?,
            date: _dateTimeConverter.decode(row['date'] as int),
            outletCode: row['outletCode'] as String,
            patientId: row['patientId'] as String,
            synced: row['synced'] == null ? null : (row['synced'] as int) != 0),
        arguments: [patientId]);
  }

  @override
  Future<void> deleteAll() async {
    await _queryAdapter.queryNoReturn('DELETE FROM Devolve');
  }

  @override
  Future<bool?> hasUnSynced() async {
    return _queryAdapter.query(
        'SELECT COUNT(*) > 0 FROM Devolve WHERE synced= 0',
        mapper: (Map<String, Object?> row) => (row.values.first as int) != 0);
  }

  @override
  Future<void> updateAllSynced() async {
    await _queryAdapter.queryNoReturn('UPDATE Devolve SET synced = 1');
  }

  @override
  Future<void> insertRecord(Devolve devolve) async {
    await _devolveInsertionAdapter.insert(devolve, OnConflictStrategy.abort);
  }
}

// ignore_for_file: unused_element
final _dateTimeConverter = DateTimeConverter();
