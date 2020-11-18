using System;
using System.IO;
using HabboWasm.Unity;

namespace HabboWasm.Patcher
{
    internal static class Program
    {
        private static void Main(string[] args)
        {
            if (args.Length != 1)
            {
                Console.WriteLine("Usage: HabboWasm.Patcher.exe <Client base directory>");
                return;
            }

            // Base client directory.
            var pathDir = args[0];

            // Client files.
            var pathAssetData = Path.Combine(pathDir, "Build", "habbo2020-global-prod.data.unityweb");
            var pathWasmFramework = Path.Combine(pathDir, "Build", "habbo2020-global-prod.wasm.framework.unityweb");
            var pathWasmCode = Path.Combine(pathDir, "Build", "habbo2020-global-prod.wasm.code.unityweb");

            // Unpack asset data.
            var unityAssetData = UnityWebData.Deserialize(pathAssetData);

            // TODO: Modify a file.
            // Data      : 67 61 6D 65 2D 2A 2E 68 61 62 62 6F 2E 63 6F 6D
            // Offset    : 0x1A61C
            // Length    : 16
            // New data  : 6C 6F 63 61 6C 68 6F 73 74
            // New length: 9
            unityAssetData.Files["Il2CppData/Metadata/global-metadata.dat"] = File.ReadAllBytes("D:\\Projects\\GitHub\\Retroo\\Habbo2020\\global-metadata.dat");

            // Repack asset data.
            unityAssetData.Serialize(pathAssetData + ".test");
        }
    }
}