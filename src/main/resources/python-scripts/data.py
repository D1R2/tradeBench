import sqlite3


#Planning to write market data to db, with each month as its own table.
#Granularity + instrument = new db. 



def processMinuteMarketData(filePath, folderPath, instrument, granularity, month, year):
#Raw data time is offset by 40,000
    dbName = filePath + "\\" + instrument + "minute" 
    tableName = instrument + str(year)
    columnArray = ["DateTime TEXT", "Open REAL", "High REAL", "Low REAL", "Close REAL",
                 "Volume INTEGER"]
    

    conn = sqlite3.connect(db)
    creatTable(conn, tableName, columnArray, primaryKey)
    
    

##def getMarketData(dataFolder, instrument, date):
##    for root, dirs, files in os.walk(folderPath):
##        for name in files:
##            if '.txt' in name && instrument in name && date in name:
##                
##


def createTable(connection, tableName, columnArray, primaryKey = True):
    
    tableString = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columnArray[0]
    
    if primaryKey == True:
        tableString += "PRIMARY KEY NOT NULL"
    
    for i in range(len(columnArray)):
        tableString += ", " + x
    tableString += ")"
    
    connection.execute(tableString)

def insertIntoTable(connection, tableName, values, columnNames = None):
    
    
    insertString = "INSERT INTO " + tableName
    
    if columnNames != None:
        insertString += " (" + columnNames[0]
        for i in range(1, len(columnNames)): 
            insertString += ", " + column
            i += 1
        insertString += ")"
        

    insertString += "VALUES (" + values[0]

    for i in range(1, len(columnNames)):
        insertString += ", " + values[i]
    
            
