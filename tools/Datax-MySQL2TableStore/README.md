# How to export data from MySQL and load into Tablestore by DataX

1. Download source code or compressed software package
    
    After getting your git environment configured, open your terminal to get source code from: 

        git clone https://github.com/alibaba/DataX.git
    
    Or you can download DataX software package directly by the link below:
    
        http://datax-opensource.oss-cn-hangzhou.aliyuncs.com/datax.tar.gz
   
    <br>
 
2. Compile the source code 
    
    After getting source code from step 1, compile it to get executable files:
        
        mvn -U clean package assembly:assembly -Dmaven.test.skip=true
    
    Or if you download Datax software directly, just uncompress it.
  
    <br>

3. Prepare a json file
    
    If you need to export data from MySQL and load into Tabelstore, a configuration file in json format is needed.
    
    Please refer to [mysql_to_ots.json](http://gitlab.alibaba-inc.com/ots/tablestore-examples/blob/guangtian/MySQL2TablestoreByDatax/tools/Datax-MySQL2TableStore/mysql_to_ots.json) 
    
    <br>
    
4.  Start to export and import
    
    After get you configuration file prepared in step 3, just start via command:
     
        python datax.py  -j"-Xms4g -Xmx4g" mysql_to_ots.json
     
   