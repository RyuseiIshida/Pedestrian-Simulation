## Multi-agent simulation tool incorporating group evacuation behavior model
JSAI2019(人工知能学会全国大会)のプログラムです。
![top-page](https://github.com/RyuseiIshida/Pedestrian-Simulation/blob/master/_SCREENSHOT/simulation.png?raw=true)   
#### 利用方法
DesktopLauncherよりビルドを行う。  
シミュレーション設定は``core/src/com/github/ryuseiishida/
pedestrian_simulation/util/Parameter.java``を書き換えて設定する。  
~~パラメータウィンドウからも設定できる。~~  
JavaFXプログラムリファクタリング中 

#### 操作
``Space``　再生・一時停止  
``Ctr+D`` シミュレーション初期化  
``Ctr+S`` エージェント位置初期化  
``Ctr+F+マウスクリック`` エージェント設置(出口を知らない避難エージェント)  
``Ctr+数字+マウルクリック``エージェント設置(出口を知っている避難エージェント)

### トピックモデルによる集団行動の解析
JSAI2020(人工知能学会全国大会)での発表予定プログラムです。
避難シミュレーションログをトピックモデルで学習することにより、
集団行動の分析を可能にします。
![lda](https://github.com/RyuseiIshida/Pedestrian-Simulation/blob/master/_SCREENSHOT/lda.png?raw=true)
#### 利用方法
シミュレーション後に``assets/out``フォルダ以下にLDA用のコーパスとして
``stepGroupSizeSplit_Corpus.txt``と``stepSplit_Corpus.txt``が生成されます。  
コーパスを[このリポジトリ](https://github.com/RyuseiIshida/simulation_analysis_lda)で使ってください。
得られた``topic.txt``を``assets``フォルダに追加し、読み込みます。
シミュレーション開始後に``Shift+L``でトピックの可視化を行うことができます。