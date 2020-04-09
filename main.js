const { app, BrowserWindow, ipcMain, remote } = require('electron');
const url = require('url');
const path = require('path');

let mainWindow;

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
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

function openModal() {
  const { BrowserWindow } = require('electron');
  let modal = new BrowserWindow({
    parent: mainWindow,
    modal: true,
    show: false,
  });
  modal.loadURL('https://www.sitepoint.com');
  modal.once('ready-to-show', () => {
    modal.show();
  });
}

ipcMain.on('openModal', (event, arg) => {
  openModal();
});

function openDirecotryWindow() {
  const { dialog } = require('electron').remote;
  dialog.showOpenDialog(
    {
      properties: ['openDirectory'],
    },
    (selectedDir) => {
      if (!selectedDir) {
        console.log('no directory is selected!');
      } else {
        console.log('selected ' + selectedDir);
      }
    }
  );
}

ipcMain.on('openDirectoryWindow', (event, arg) => {
  openDirecotryWindow();
});
