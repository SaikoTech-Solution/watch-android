import 'package:flutter/services.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Method Channel Demo'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  var deviceName = "";
  var connectionState = "";
  var heartRate = "";
  var bloodPressure = "";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              onPressed: () async {
                var result = await startScan();
                setState(() {
                  deviceName = result;
                });
              },
              child: const Text('SCAN DEVICE'),
            ),
            Text(
              'Device:  $deviceName',
              style: const TextStyle(
                color: Colors.blue,
                fontSize: 10,
              ),
            ),
            ElevatedButton(
              onPressed: () async {
                var result = await startConnection();
                setState(() {
                  connectionState = result;
                });
              },
              child: const Text('Start Connection'),
            ),
            Text(
              'Connection Status :  $connectionState',
              style:const TextStyle(
                color: Colors.blue,
                fontSize: 10,
              ),
            ),
            ElevatedButton(
              onPressed: () async {
                var result = await getHeartRate();
                setState(() {
                  heartRate = result;
                });
              },
              child: const Text('Get Heart Rate'),
            ),
            Text(
              'Heart Rate:  $heartRate',
              style:const TextStyle(
                color: Colors.blue,
                fontSize: 10,
                backgroundColor: Colors.yellow,
              ),
            ),
            ElevatedButton(
              onPressed: () async {
                var result = await getBloodPressure();
                setState(() {
                  bloodPressure = result;
                });
              },
              child: const Text('Get Blood Pressure'),
            ),
            Text(
              'Heart Rate:  $bloodPressure',
              style:const TextStyle(
                color: Colors.blue,
                fontSize: 10,
                backgroundColor: Colors.yellow,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

//=========Handle Deep Linking=========//
var methodChannel = const MethodChannel('flutter_android_watch');

Future<String> startScan() async {
  try {
    var data = await methodChannel.invokeMethod('startScan');

    return data;
  } on PlatformException catch (e) {
    return "Failed to Invoke: '${e.message}'.";
  }
}

Future<String> startConnection() async {
  try {
    var arguments = {'deviceAddress': "D6:BE:BF:8E:DC:81"};
    var data = await methodChannel.invokeMethod('startConnection', arguments);

    return data;
  } on PlatformException catch (e) {
    return "Failed to Invoke: '${e.message}'.";
  }
}

Future<String> getHeartRate() async {
  try {
    var arguments = {'date': '18-12-2022'};
    var data = await methodChannel.invokeMethod('getHeartRate', arguments);
    return data;
  } on PlatformException catch (e) {
    return "Failed to Invoke: '${e.message}'.";
  }
}
Future<String> getBloodPressure() async {
  try {
    var arguments = {'date': '18-12-2022'};
    var data = await methodChannel.invokeMethod('getBloodPressure', arguments);
    return data;
  } on PlatformException catch (e) {
    return "Failed to Invoke: '${e.message}'.";
  }
}