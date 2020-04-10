const { app, BrowserWindow, ipcMain, dialog, screen } = require('electron');
const url = require('url');
const path = require('path');

let mainWindow;

function createWindow() {
  var size = screen.getPrimaryDisplay().workAreaSize;
  mainWindow = new BrowserWindow({
    x: 10,
    y: 15,
    width: size.width - 100,
    height: size.height - 100,
    webPreferences: {
      nodeIntegration: true,
    },
  });

  mainWindow.loadURL(
    url.format({
      pathname: path.join(__dirname, `/dist/index.html`),
      protocol: 'file:',
      slashes: true,
    })
  );
  // Open the DevTools.
  mainWindow.webContents.openDevTools();

  mainWindow.on('closed', function () {
    mainWindow = null;
  });
}

app.on('ready', createWindow);

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit();
});

app.on('activate', function () {
  if (mainWindow === null) createWindow();
});

ipcMain.on('OpenDirecotryDialog', async (event, arg) => {
  const result = await dialog.showOpenDialog({
    properties: ['openDirectory'],
  });
  mainWindow.webContents.send('CloseDirecotryDialog', result.filePaths);
});

