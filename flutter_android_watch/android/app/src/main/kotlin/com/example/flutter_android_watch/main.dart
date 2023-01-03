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
  String deviceName = "";

  @override  Widget build(BuildContext context) {

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'Demo Application for method channel',
            ),
            Text(
              deviceName,
            ),
            ElevatedButton(onPressed: () async {
              print(await callNativeCode());

            },
            child: const Text('Call Method Channel'),)
          ],
        ),
      ),
    );
  }
}

//=========Handle Deep Linking=========//
var methodChannel = const MethodChannel('flutter_android_watch');

Future<String> callNativeCode() async {
  try {
    var data = await methodChannel.invokeMethod('startScan');
    return data;
  } on PlatformException catch (e) {
    return "Failed to Invoke: '${e.message}'.";
  }
}
