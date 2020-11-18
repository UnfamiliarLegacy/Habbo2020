using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using HabboWasm.Unity.Exceptions;
using HabboWasm.Unity.Extensions;

namespace HabboWasm.Unity
{
    public class UnityWebData
    {
        public UnityWebData()
        {
            Signature = string.Empty;
            Files = new Dictionary<string, byte[]>();
        }

        public string Signature { get; set; }

        public Dictionary<string, byte[]> Files { get; set; }

        public static UnityWebData Deserialize(string file)
        {
            using var stream = File.OpenRead(file);
            using var reader = new BinaryReader(stream);

            var result = new UnityWebData
            {
                Signature = reader.ReadStringNullTerminated()
            };

            // Read header length.
            var headerLength = reader.ReadUInt32();

            while (reader.BaseStream.Position < headerLength)
            {
                // Read header information.
                var offset = reader.ReadUInt32();
                var size = reader.ReadUInt32();
                var nameLen = reader.ReadInt32();
                var name = Encoding.UTF8.GetString(reader.ReadBytes(nameLen));

                if (result.Files.ContainsKey(name))
                {
                    throw new UnityException($"Duplicate files in UnityWebData \"{name}\".");
                }

                // Save position.
                var oldPos = reader.BaseStream.Position;

                // Read file.
                reader.BaseStream.Position = offset;
                result.Files.Add(name, reader.ReadBytes((int) size));

                // Restore position.
                reader.BaseStream.Position = oldPos;
            }

            return result;
        }

        public void Serialize(string file)
        {
            using var stream = File.OpenWrite(file);
            using var writer = new BinaryWriter(stream);

            writer.WriteStringNullTerminated(Signature);

            // Prepare header.
            var headerPos = writer.BaseStream.Position;
            var headerFileOffsets = new Dictionary<string, long>();

            writer.Write((uint) 0);

            foreach (var (name, _) in Files)
            {
                headerFileOffsets.Add(name, writer.BaseStream.Position);

                writer.Write((uint) 0);
                writer.Write((uint) 0);
                writer.Write((int) name.Length);
                writer.Write(Encoding.UTF8.GetBytes(name));
            }

            var headerLen = writer.BaseStream.Position;

            // Write header length.
            writer.BaseStream.Position = headerPos;
            writer.Write((uint) headerLen);
            writer.BaseStream.Position = headerLen;

            // Write all files and update offset in header.
            foreach (var (name, data) in Files)
            {
                var fileSize = data.Length;
                var fileOffset = writer.BaseStream.Position;

                writer.Write(data);

                // Update header.
                writer.BaseStream.Position = headerFileOffsets[name];
                writer.Write((uint) fileOffset);
                writer.Write((uint) fileSize);
                writer.BaseStream.Position = fileOffset + fileSize;
            }
        }
    }
}